/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.ExamTypeConstant;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.config.ExamSpecialConfig;
import top.continew.admin.config.InspectorConfig;
import top.continew.admin.document.mapper.DocumentMapper;
import top.continew.admin.document.mapper.ExamineeDocumentMapper;
import top.continew.admin.document.model.dto.CategoryNoticeTreeDTO;
import top.continew.admin.document.model.entity.DocumentDO;
import top.continew.admin.document.model.entity.ExamineeDocumentDO;
import top.continew.admin.document.model.resp.CategoryNoticeTreeVO;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.*;
import top.continew.admin.exam.model.req.dto.ProjectApplyDTO;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.service.LicenseHolderInfoService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.vo.UploadWhenUserInfoVO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.ExamNoticeQuery;
import top.continew.admin.exam.service.ExamNoticeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 无损检测、检验人员考试通知业务实现
 *
 * @author ilhaha
 * @since 2026/04/14 15:20
 */
@Service
@RequiredArgsConstructor
public class ExamNoticeServiceImpl extends BaseServiceImpl<ExamNoticeMapper, ExamNoticeDO, ExamNoticeResp, ExamNoticeDetailResp, ExamNoticeQuery, ExamNoticeReq> implements ExamNoticeService {

    private final ExamSpecialConfig examSpecialConfig;

    private final ExamPlanMapper examPlanMapper;

    private final ExamNoticePlanMapper examNoticePlanMapper;

    private final ProjectMapper projectMapper;

    private final UserMapper userMapper;

    private final ExamIdcardMapper examIdcardMapper;

    private final AESWithHMAC aesWithHMAC;

    private final DocumentMapper documentMapper;

    private final ExamineeDocumentMapper examineeDocumentMapper;

    private final ExamineeNoticeApplyMapper examineeNoticeApplyMapper;

    private final ExamineeNoticeApplyRecordMapper examineeNoticeApplyRecordMapper;

    private final InspectorConfig inspectorConfig;

    private final LicenseHolderInfoService licenseHolderInfoService;

    private final CategoryMapper categoryMapper;

    /**
     * 重写page
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamNoticeResp> page(ExamNoticeQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamNoticeDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("ten.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<ExamNoticeDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<ExamNoticeResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 重写修改
     *
     * @param req
     * @param id
     */
    @Override
    public void update(ExamNoticeReq req, Long id) {
        // 判断标题是否存在
        ValidationUtils.throwIf(baseMapper.selectCount(new LambdaQueryWrapper<ExamNoticeDO>()
                .eq(ExamNoticeDO::getTitle, req.getTitle())
                .ne(ExamNoticeDO::getId, id)) > 0, "标题已存在");
        Long categoryId = req.getCategoryId();
        List<ExamNoticeExamProjectReq> projectList = req.getProjectList();
        ValidationUtils.throwIf(ObjectUtil.isEmpty(projectList), "未选择报考项目");
        // 先删除之前的关系
        deleteNoticeAndPlan(CollectionUtil.toList(id));
        boolean isExamptRenew = examSpecialConfig.getExemptRenewIds().contains(categoryId);
        addExamNoticeAndPlan(req, projectList, isExamptRenew, id);
        req.setStatus(NoticeAuditStatusEnum.PENDING.getValue());
        super.update(req, id);
    }

    /**
     * 重写删除
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {

        deleteNoticeAndPlan(ids);

        // 3 最后删通知
        super.delete(ids);
    }

    private void deleteNoticeAndPlan(List<Long> ids) {
        List<ExamNoticePlanDO> relations = examNoticePlanMapper.selectList(new LambdaQueryWrapper<ExamNoticePlanDO>()
                .in(ExamNoticePlanDO::getNoticeId, ids));

        if (CollUtil.isNotEmpty(relations)) {

            List<Long> planIds = relations.stream().map(ExamNoticePlanDO::getPlanId).distinct().toList();

            // 1 先删关联表
            examNoticePlanMapper.deleteByIds(relations.stream().map(ExamNoticePlanDO::getId).toList());

            // 2 再删计划
            if (CollUtil.isNotEmpty(planIds)) {
                examPlanMapper.deleteByIds(planIds);
            }
        }
    }

    /**
     * 重写详情
     *
     * @param id
     * @return
     */
    @Override
    public ExamNoticeDetailResp get(Long id) {
        ExamNoticeDetailResp examNoticeDetailResp = super.get(id);
        // 查询当前通知对应的计划信息
        List<ExamNoticePlanDO> examNoticePlanDOS = examNoticePlanMapper
                .selectList(new LambdaQueryWrapper<ExamNoticePlanDO>().eq(ExamNoticePlanDO::getNoticeId, id));
        List<Long> planId = examNoticePlanDOS.stream().map(ExamNoticePlanDO::getPlanId).toList();
        List<ExamPlanDO> examPlanDOS = examPlanMapper.selectByIds(planId);
        List<ExamNoticeExamProjectResp> projectList = examPlanDOS.stream().map(item -> {
            ExamNoticeExamProjectResp examNoticeExamProjectResp = new ExamNoticeExamProjectResp();
            examNoticeExamProjectResp.setExamTime(item.getStartTime());
            examNoticeExamProjectResp.setProjectId(item.getExamProjectId());
            return examNoticeExamProjectResp;
        }).toList();
        examNoticeDetailResp.setProjectList(projectList);

        return examNoticeDetailResp;
    }

    /**
     * 重写添加
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(ExamNoticeReq req) {
        ValidationUtils.throwIf(baseMapper.selectCount(new LambdaQueryWrapper<ExamNoticeDO>()
                .eq(ExamNoticeDO::getTitle, req.getTitle())) > 0, "标题已存在");
        Long categoryId = req.getCategoryId();
        List<ExamNoticeExamProjectReq> projectList = req.getProjectList();
        ValidationUtils.throwIf(ObjectUtil.isEmpty(projectList), "未选择报考项目");
        boolean isExamptRenew = examSpecialConfig.getExemptRenewIds().contains(categoryId);
        Long reqId = super.add(req);
        addExamNoticeAndPlan(req, projectList, isExamptRenew, reqId);
        return reqId;
    }

    private void addExamNoticeAndPlan(ExamNoticeReq req,
                                      List<ExamNoticeExamProjectReq> projectList,
                                      boolean isExamptRenew,
                                      Long noticeId) {
        // 添加计划
        List<ExamPlanDO> planInsertList = projectList.stream().map(item -> {
            ExamPlanDO examPlanDO = new ExamPlanDO();
            examPlanDO.setExamPlanName(req.getTitle() + " - " + item.getProjectCode());
            examPlanDO.setExamProjectId(item.getProjectId());
            LocalDateTime startTime = isExamptRenew ? LocalDateTime.now() : item.getExamTime();
            examPlanDO.setStartTime(startTime);
            examPlanDO.setPlanYear(String.valueOf(startTime.getYear()));
            examPlanDO.setMaxCandidates(0);
            examPlanDO.setPlanType(ExamPlanTypeEnum.INSPECTION.getValue());
            examPlanDO.setStatus(ExamPlanStatusEnum.IN_FORCE.getValue());
            return examPlanDO;
        }).toList();
        examPlanMapper.insertBatch(planInsertList);
        // 添加计划与通知关系表
        List<ExamNoticePlanDO> noticePlanInsertList = planInsertList.stream().map(item -> {
            ExamNoticePlanDO examNoticePlanDO = new ExamNoticePlanDO();
            examNoticePlanDO.setNoticeId(noticeId);
            examNoticePlanDO.setPlanId(item.getId());
            examNoticePlanDO.setProjectId(item.getExamProjectId());
            return examNoticePlanDO;
        }).toList();
        examNoticePlanMapper.insertBatch(noticePlanInsertList);
    }

    /**
     * 审核
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditExamNotice(ExamNoticeAuditReq req) {
        List<ExamNoticeDO> examNoticeDOS = baseMapper.selectByIds(req.getIds());
        ValidationUtils.throwIfEmpty(examNoticeDOS, "所选审核数据不存在");
        List<ExamNoticeDO> updateList = examNoticeDOS.stream().map(item -> {
            ExamNoticeDO examNoticeDO = new ExamNoticeDO();
            examNoticeDO.setId(item.getId());
            examNoticeDO.setStatus(req.getStatus());
            return examNoticeDO;
        }).toList();
        return baseMapper.updateBatchById(updateList);
    }

    /**
     * 检验人员查看通知列表
     *
     * @param examNoticeQuery
     * @param pageQuery
     * @return 分页结果
     */
    @Override
    public PageResp<ExamNoticeResp> inspectionGetNoticeList(ExamNoticeQuery examNoticeQuery, PageQuery pageQuery) {
        QueryWrapper<ExamNoticeDO> queryWrapper = this.buildQueryWrapper(examNoticeQuery);
        queryWrapper.eq("ten.status", NoticeStatusEnum.APPROVED.getValue())
                .eq("ten.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<ExamNoticeDetailResp> page = baseMapper.inspectionGetNoticeList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<ExamNoticeResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 检验人员报名时查看通知的详细内容
     *
     * @param noticeId
     * @return
     */
    @Override
    public NoticeApplyInfoResp getNoticeApplyInfo(Long noticeId) {

        Long userId = TokenLocalThreadUtil.get().getUserId();

        UserDO userDO = userMapper.selectById(userId);

        ValidationUtils.throwIfNull(userDO, "未登录");

        // 通知
        ExamNoticeDO examNoticeDO = baseMapper.selectById(noticeId);

        ValidationUtils.throwIfNull(examNoticeDO, "通知不存在");

        // 查询项目
        List<ProjectDO> projectDOS = projectMapper.selectList(
                new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getCategoryId, examNoticeDO.getCategoryId())
                        .eq(ProjectDO::getProjectLevel, examNoticeDO.getExamLevel())
        );

        ValidationUtils.throwIfEmpty(projectDOS, "该通知未绑定考试项目");

        NoticeApplyInfoResp resp = new NoticeApplyInfoResp();

        BeanUtil.copyProperties(examNoticeDO, resp);

        List<NoticeProjectResp> projectRespList = projectDOS.stream().map(item -> {

            NoticeProjectResp projectResp = new NoticeProjectResp();

            BeanUtil.copyProperties(item, projectResp);

            return projectResp;

        }).toList();

        resp.setProjectRespList(projectRespList);

        /**
         * 项目Map
         */
        Map<Long, NoticeProjectResp> projectMap =
                projectRespList.stream().collect(Collectors.toMap(
                        NoticeProjectResp::getId,
                        Function.identity()
                ));

        /**
         * 学历认证
         */
        ExamIdcardDO examIdcardDO = examIdcardMapper.selectOne(
                new LambdaQueryWrapper<ExamIdcardDO>()
                        .eq(ExamIdcardDO::getIdCardNumber, userDO.getUsername())
        );

        NoticeCandidateResp noticeCandidateResp = new NoticeCandidateResp();

        if (ObjectUtil.isNotNull(examIdcardDO)) {

            noticeCandidateResp.setEducationVerifyStatus(
                    examIdcardDO.getEducationVerifyStatus()
            );

            noticeCandidateResp.setEducation(
                    examIdcardDO.getEducation()
            );
        }

        resp.setNoticeCandidateResp(noticeCandidateResp);

        /**
         * 查询通知计划
         */
        List<ExamNoticePlanDO> noticePlanDOS =
                examNoticePlanMapper.selectList(
                        new LambdaQueryWrapper<ExamNoticePlanDO>()
                                .eq(ExamNoticePlanDO::getNoticeId, noticeId)
                );

        if (ObjectUtil.isNotEmpty(noticePlanDOS)) {

            List<Long> planIds = noticePlanDOS.stream()
                    .map(ExamNoticePlanDO::getPlanId)
                    .toList();

            List<ExamPlanDO> examPlanDOS =
                    examPlanMapper.selectBatchIds(planIds);

            Map<Long, ExamPlanDO> planMap =
                    examPlanDOS.stream().collect(Collectors.toMap(
                            ExamPlanDO::getId,
                            Function.identity()
                    ));

            for (ExamNoticePlanDO noticePlanDO : noticePlanDOS) {

                ExamPlanDO planDO = planMap.get(noticePlanDO.getPlanId());

                if (ObjectUtil.isNull(planDO)) {
                    continue;
                }

                NoticeProjectResp projectResp =
                        projectMap.get(planDO.getExamProjectId());

                if (ObjectUtil.isNotNull(projectResp)) {

                    projectResp.setStartTime(planDO.getStartTime());

                    projectResp.setPlanId(planDO.getId());
                }
            }
        }

        /**
         * 查询报名主表
         */
        ExamineeNoticeApplyDO applyDO =
                examineeNoticeApplyMapper.selectOne(
                        new LambdaQueryWrapper<ExamineeNoticeApplyDO>()
                                .eq(ExamineeNoticeApplyDO::getExamineeId, userId)
                                .eq(ExamineeNoticeApplyDO::getNoticeId, noticeId)
                );

        if (ObjectUtil.isNull(applyDO)) {
            return resp;
        }

        ExamineeNoticeApplyResp applyResp =
                new ExamineeNoticeApplyResp();

        BeanUtil.copyProperties(applyDO, applyResp);

        /**
         * 查询报名记录
         */
        List<ExamineeNoticeApplyRecordDO> recordDOS =
                examineeNoticeApplyRecordMapper.selectList(
                        new LambdaQueryWrapper<ExamineeNoticeApplyRecordDO>()
                                .eq(ExamineeNoticeApplyRecordDO::getApplyId, applyDO.getId())
                );

        if (ObjectUtil.isNotEmpty(recordDOS)) {

            applyResp.setNoticeApplyRecordRespList(
                    recordDOS.stream().map(item -> {

                        ExamineeNoticeApplyRecordResp recordResp =
                                new ExamineeNoticeApplyRecordResp();

                        BeanUtil.copyProperties(item, recordResp);

                        return recordResp;

                    }).toList()
            );
        }

        resp.setExamineeNoticeApplyResp(applyResp);

        return resp;
    }

    /**
     * 报考通知
     *
     * @param examApplyReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean apply(ExamApplyReq examApplyReq) {

        Long userId = TokenLocalThreadUtil.get().getUserId();

        UserDO userDO = userMapper.selectById(userId);

        ValidationUtils.throwIfNull(userDO, "未登录");

        ExamNoticeDO examNoticeDO = baseMapper.selectById(examApplyReq.getNoticeId());

        ValidationUtils.throwIfNull(examNoticeDO, "所报考通知不存在");

        Long noticeId = examNoticeDO.getId();

        LocalDate applyDeadline = examNoticeDO.getApplyDeadline();

        ValidationUtils.throwIf(
                LocalDate.now().isAfter(applyDeadline),
                "该考试通知已截止报名"
        );

        List<ProjectApplyDTO> projectList = examApplyReq.getProjectList();

        ValidationUtils.throwIfEmpty(projectList, "未选择报考项目");

        // 查询用户实名制信息
        ExamIdcardDO examIdcardDO = examIdcardMapper.selectOne(
                new LambdaQueryWrapper<ExamIdcardDO>()
                        .eq(ExamIdcardDO::getIdCardNumber, userDO.getUsername())
        );
        ValidationUtils.throwIfNull(examNoticeDO, "未进行实名");
        String idCardNumber = aesWithHMAC.verifyAndDecrypt(examIdcardDO.getIdCardNumber());
        // 提取出生日期（18位身份证：第7-14位）
        String birthStr = idCardNumber.substring(6, 14);

        // 转 LocalDate
        LocalDate birthDate = LocalDate.parse(
                birthStr,
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );

        // 计算年龄
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 项目ID列表
        List<Long> projectIds = projectList.stream()
                .map(ProjectApplyDTO::getProjectId)
                .distinct()
                .collect(Collectors.toList());

        // 查询项目信息
        List<ProjectDO> projectDOList = projectMapper.selectList(
                new LambdaQueryWrapper<ProjectDO>()
                        .in(ProjectDO::getId, projectIds)
        );

        ValidationUtils.throwIfEmpty(projectDOList, "所选报考项目信息不存在");
        for (ProjectDO projectDO : projectDOList) {
            Integer examEndAge = projectDO.getExamEndAge();
            ValidationUtils.throwIf(
                    age > examEndAge,
                    "当前年龄【" + age + "】岁，已超过【"
                            + projectDO.getProjectCode()
                            + "】允许报考年龄【"
                            + examEndAge + "】岁"
            );
        }

        // 报名主表
        ExamineeNoticeApplyDO examineeNoticeApplyDO =
                examineeNoticeApplyMapper.selectOne(
                        new LambdaQueryWrapper<ExamineeNoticeApplyDO>()
                                .eq(ExamineeNoticeApplyDO::getExamineeId, userId)
                                .eq(ExamineeNoticeApplyDO::getNoticeId, noticeId)
                );

        ValidationUtils.throwIfNull(
                examineeNoticeApplyDO,
                "请先完善报名资料"
        );

        // 校验资料上传的对不对
        licenseHolderInfoService.checkLicenseCertificateUploaded(examIdcardDO,);

        List<ExamineeNoticeApplyRecordDO> applyRecordDOS =
                projectList.stream().map(item -> {

                    Long projectId = item.getProjectId();

                    Integer examAttemptType = item.getExamAttemptType();

                    // 初考只能报一次
                    Long firstExamCount =
                            examineeNoticeApplyRecordMapper.selectCount(
                                    new LambdaQueryWrapper<ExamineeNoticeApplyRecordDO>()
                                            .eq(ExamineeNoticeApplyRecordDO::getExamineeId, userId)
                                            .eq(ExamineeNoticeApplyRecordDO::getProjectId, projectId)
                                            .eq(
                                                    ExamineeNoticeApplyRecordDO::getExamAttemptType,
                                                    ExamAttemptTypeEnum.FIRST_EXAM.getValue()
                                            )
                            );

                    ValidationUtils.throwIf(
                            firstExamCount > 0
                                    && ExamAttemptTypeEnum.FIRST_EXAM.getValue()
                                    .equals(examAttemptType),
                            "项目【" + item.getProjectCode() + "】初考已报名，请勿重复报名"
                    );

                    // 当前通知下不能重复报名
                    Long currentNoticeCount =
                            examineeNoticeApplyRecordMapper.selectCount(
                                    new LambdaQueryWrapper<ExamineeNoticeApplyRecordDO>()
                                            .eq(ExamineeNoticeApplyRecordDO::getExamineeId, userId)
                                            .eq(ExamineeNoticeApplyRecordDO::getNoticeId, noticeId)
                                            .eq(ExamineeNoticeApplyRecordDO::getProjectId, projectId)
                                            .eq(
                                                    ExamineeNoticeApplyRecordDO::getExamAttemptType,
                                                    examAttemptType
                                            )
                            );

                    ValidationUtils.throwIf(
                            currentNoticeCount > 0,
                            "已报名通知下的【" + item.getProjectCode() + "】，请勿重复提交"
                    );

                    // TODO 补充补考逻辑

                    // 通知计划
                    ExamNoticePlanDO examNoticePlanDO =
                            examNoticePlanMapper.selectOne(
                                    new LambdaQueryWrapper<ExamNoticePlanDO>()
                                            .eq(ExamNoticePlanDO::getNoticeId, noticeId)
                                            .eq(ExamNoticePlanDO::getProjectId, projectId)
                            );

                    ValidationUtils.throwIfNull(
                            examNoticePlanDO,
                            "该报考通知未绑定【" + item.getProjectCode() + "】考试项目"
                    );



                    ExamineeNoticeApplyRecordDO recordDO =
                            new ExamineeNoticeApplyRecordDO();


                    recordDO.setApplyId(examineeNoticeApplyDO.getId());

                    recordDO.setExamineeId(userDO.getId());

                    recordDO.setNoticeId(examNoticePlanDO.getNoticeId());

                    recordDO.setProjectId(examNoticePlanDO.getProjectId());

                    recordDO.setPlanId(examNoticePlanDO.getPlanId());

                    // 初考/补考
                    recordDO.setExamAttemptType(item.getExamAttemptType());

                    // 位运算值
                    recordDO.setPracticalType(item.getPracticalType());

                    return recordDO;

                }).toList();

        examineeNoticeApplyRecordMapper.insertBatch(applyRecordDOS);

        // 修改报名状态
        ExamineeNoticeApplyDO updateApply = new ExamineeNoticeApplyDO();

        updateApply.setId(examineeNoticeApplyDO.getId());

        updateApply.setStatus(
                ExamineeNoticeApplyStatusEnum.PENDING_REVIEW.getValue()
        );

        examineeNoticeApplyMapper.updateById(updateApply);

        return Boolean.TRUE;
    }

    /**
     * 获取已发布的分类-级别-通知级联选择器
     *
     * @return
     */
    @Override
    public List<CategoryNoticeTreeVO> getCategoryNoticeTree() {

        List<CategoryNoticeTreeDTO> list = baseMapper.getCategoryNoticeTree();

        if (ObjectUtil.isEmpty(list))
            return Collections.emptyList();

        // 分类分组
        Map<Long, List<CategoryNoticeTreeDTO>> categoryMap = list.stream()
                .collect(Collectors.groupingBy(CategoryNoticeTreeDTO::getCategoryId));

        List<CategoryNoticeTreeVO> result = new ArrayList<>();

        for (Map.Entry<Long, List<CategoryNoticeTreeDTO>> categoryEntry : categoryMap.entrySet()) {

            List<CategoryNoticeTreeDTO> categoryList = categoryEntry.getValue();

            CategoryNoticeTreeDTO firstCategory = categoryList.get(0);

            // 分类节点
            CategoryNoticeTreeVO categoryNode = new CategoryNoticeTreeVO();
            categoryNode.setValue(firstCategory.getCategoryId());
            categoryNode.setLabel(firstCategory.getCategoryName());

            // 提取方法
            categoryNode.setChildren(buildLevelChildren(categoryList));

            result.add(categoryNode);
        }

        return result;
    }

    /**
     * 根据通知id获取考生已上传的资料列表、未上传的资料列表
     *
     * @param noticeId
     * @return
     */
    @Override
    public NoticeUploadInfoResp getNoticeAndDocInfo(Long noticeId) {

        Long userId = TokenLocalThreadUtil.get().getUserId();

        UserDO userDO = userMapper.selectById(userId);

        ValidationUtils.throwIfNull(userDO, "未登录");

        ExamNoticeDO examNoticeDO = baseMapper.selectById(noticeId);
        ValidationUtils.throwIfNull(examNoticeDO, "报考通知不存在");

        // 查出通知对应的项目
        List<ProjectResp> projectByNoticeId = getProjectByNoticeId(noticeId);

        if (ObjectUtil.isEmpty(projectByNoticeId)) {
            return null;
        }

        List<Long> projectIds = projectByNoticeId.stream()
                .map(ProjectResp::getId)
                .toList();

        // 非必须上传资料（yml配置）
        List<UploadedDocumentTypeVO> optionalUploadDocs =
                inspectorConfig.getOptionalUploadDocs();
        // 判断当前通知是否需要上传证书
        boolean isNoUpload = isNoNeedUploadCertificate(examNoticeDO);


        // 非必须上传资料ID集合
        Set<Long> optionalDocIds = optionalUploadDocs.stream()
                .map(UploadedDocumentTypeVO::getId)
                .collect(Collectors.toSet());

        // 已上传资料
        Set<UploadedDocumentTypeVO> alreadyUploadDocList =
                baseMapper.getAlreadyUploadDocList(userId, noticeId);

        /*
         * =========================
         * 处理必须上传资料
         * =========================
         */

        // 需要上传的资料（过滤掉非必须）
        Set<UploadedDocumentTypeVO> needUploadDocList =
                projectMapper.getProjectBindingDocumentByIds(projectIds)
                        .stream()
                        .filter(item -> !optionalDocIds.contains(item.getId()))
                        .collect(Collectors.toSet());

        // 必须资料并集
        Map<Long, UploadedDocumentTypeVO> unionMap = new LinkedHashMap<>();

        // 先放项目固定绑定的
        needUploadDocList.forEach(item ->
                unionMap.put(item.getId(), item));

        // 再放已上传的（如果存在则覆盖）
        alreadyUploadDocList.stream()
                .filter(item -> !optionalDocIds.contains(item.getId()))
                .forEach(item -> unionMap.put(item.getId(), item));

        List<UploadedDocumentTypeVO> unionList =
                new ArrayList<>(unionMap.values());

        /*
         * =========================
         * 处理非必须上传资料
         * =========================
         */

        // key=id
        Map<Long, UploadedDocumentTypeVO> optionalMap =
                optionalUploadDocs.stream()
                        .collect(Collectors.toMap(
                                UploadedDocumentTypeVO::getId,
                                item -> item,
                                (a, b) -> b,
                                LinkedHashMap::new
                        ));

        // 如果用户上传过，则覆盖路径等信息
        alreadyUploadDocList.stream()
                .filter(item -> optionalDocIds.contains(item.getId()))
                .forEach(item -> optionalMap.put(item.getId(), item));

        List<UploadedDocumentTypeVO> optionalList =
                new ArrayList<>(optionalMap.values());

        /*
         * =========================
         * 返回
         * =========================
         */

        NoticeUploadInfoResp resp = new NoticeUploadInfoResp();

        // 必须上传资料
        resp.setDocList(unionList);

        // 非必须上传资料
        resp.setOptionalDocList(isNoUpload ? Collections.emptyList() : optionalList);

        // 找出考生实名制信息
        ExamIdcardDO examIdcardDO = examIdcardMapper.selectOne(
                new LambdaQueryWrapper<ExamIdcardDO>()
                        .eq(ExamIdcardDO::getIdCardNumber, userDO.getUsername())
        );

        if (ObjectUtil.isNull(examIdcardDO)) {
            return resp;
        }

        resp.setIdCardPhotoFront(examIdcardDO.getIdCardPhotoFront());
        resp.setIdCardPhotoBack(examIdcardDO.getIdCardPhotoBack());
        resp.setEducation(examIdcardDO.getEducation());
        resp.setMajorType(examIdcardDO.getMajorType());
        resp.setFacePhoto(examIdcardDO.getFacePhoto());
        resp.setIdNumber(
                aesWithHMAC.verifyAndDecrypt(examIdcardDO.getIdCardNumber())
        );

        // 找出这个考生是否对这个通知上传的资料信息
        ExamineeNoticeApplyDO examineeNoticeApplyDO =
                examineeNoticeApplyMapper.selectOne(
                        new LambdaQueryWrapper<ExamineeNoticeApplyDO>()
                                .eq(ExamineeNoticeApplyDO::getNoticeId, noticeId)
                                .eq(ExamineeNoticeApplyDO::getExamineeId, userDO.getId())
                );

        if (ObjectUtil.isNull(examineeNoticeApplyDO)) {
            return resp;
        }

        resp.setApplyStatus(examineeNoticeApplyDO.getStatus());
        resp.setApplyReason(examineeNoticeApplyDO.getRemark());

        return resp;
    }

    /**
     * 判断通知是否需要上传证书
     *
     * @param examNoticeDO
     * @return
     */
    private boolean isNoNeedUploadCertificate(ExamNoticeDO examNoticeDO) {
        // 不需要上传证书的类别
        List<Long> noUploadCategoryIds = inspectorConfig.getNoUploadCategoryIds();
        // 如果通知属于一级，且分类属于不需要上传证书的类别，不需要过滤和返回非必须资料
        return noUploadCategoryIds.contains(examNoticeDO.getCategoryId())
                && ProjectLevelEnum.LEVEL_ONE.getValue().equals(examNoticeDO.getExamLevel());
    }

    /**
     * 获取通知对应的项目
     *
     * @param noticeId
     * @return
     */
    @Override
    public List<ProjectResp> getProjectByNoticeId(Long noticeId) {
        ExamNoticeDO examNoticeDO = baseMapper.selectById(noticeId);
        ValidationUtils.throwIfNull(examNoticeDO, "报考通知信息不存在");
        return baseMapper.getProjectByNoticeId(noticeId);
    }

    /**
     * 上传通知资料
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean uploadSubmit(NoticeUploadDocReq req) {

        Long userId = TokenLocalThreadUtil.get().getUserId();

        UserDO userDO = userMapper.selectById(userId);

        ValidationUtils.throwIfNull(userDO, "未登录");

        Long noticeId = req.getNoticeId();

        ExamNoticeDO examNoticeDO = baseMapper.selectById(noticeId);

        ValidationUtils.throwIfNull(examNoticeDO, "报考通知不存在");

        // 实名信息
        ExamIdcardDO examIdcardDO = examIdcardMapper.selectOne(
                new LambdaQueryWrapper<ExamIdcardDO>()
                        .eq(ExamIdcardDO::getIdCardNumber, userDO.getUsername())
        );

        ValidationUtils.throwIfNull(examIdcardDO, "实名信息不存在");


        List<DocumentFileReq> docFileList = req.getDocFileList();

        if (ObjectUtil.isEmpty(docFileList)) {
            return Boolean.TRUE;
        }

        // 判断当前通知是否需要上传证书
        boolean isNoUpload = isNoNeedUploadCertificate(examNoticeDO);

        if (!isNoUpload) {

            // 非必须上传的证书列表
            List<UploadedDocumentTypeVO> optionalUploadDocs =
                    inspectorConfig.getOptionalUploadDocs();

            // 当前上传的资料类型ID
            Set<Long> uploadedTypeIds = docFileList.stream()
                    .map(DocumentFileReq::getTypeId)
                    .collect(Collectors.toSet());

            // 过滤出当前上传了的证书资料
            List<UploadedDocumentTypeVO> uploadedCertificateList =
                    optionalUploadDocs.stream()
                            .filter(item -> uploadedTypeIds.contains(item.getId()))
                            .toList();
            Long categoryId = examNoticeDO.getCategoryId();
            // 判断是否是取证考试
            Boolean isCertificateExam = categoryMapper.selectCount(new LambdaQueryWrapper<CategoryDO>()
                    .eq(CategoryDO::getId, categoryId)
                    .eq(CategoryDO::getExamType, ExamTypeConstant.CERTIFICATE_EXAM)) > 0;

            // 根据实名校验是否存在持证信息
            licenseHolderInfoService.checkLicenseCertificateUploaded(examIdcardDO, uploadedCertificateList, isCertificateExam);
        }

        ExamIdcardDO updateExamIdCard = new ExamIdcardDO();
        updateExamIdCard.setId(examIdcardDO.getId());
        updateExamIdCard.setFacePhoto(req.getFacePhoto());
        updateExamIdCard.setIdCardPhotoFront(req.getIdCardPhotoFront());
        updateExamIdCard.setIdCardPhotoBack(req.getIdCardPhotoBack());
        examIdcardMapper.updateById(updateExamIdCard);

        // 先查出考生有没有上传过资料，如果有就先删掉之前的
        LambdaQueryWrapper<ExamineeDocumentDO> examineeDocumentDOLambdaQueryWrapper = new LambdaQueryWrapper<ExamineeDocumentDO>()
                .eq(ExamineeDocumentDO::getExamineeId, userId)
                .eq(ExamineeDocumentDO::getNoticeId, noticeId);
        List<ExamineeDocumentDO> examineeDocumentDOS = examineeDocumentMapper.selectList(examineeDocumentDOLambdaQueryWrapper);
        if (ObjectUtil.isNotEmpty(examineeDocumentDOS)) {
            List<Long> documentIds = examineeDocumentDOS.stream().map(ExamineeDocumentDO::getDocumentId).toList();
            // 删除资料
            documentMapper.deleteByIds(documentIds);
            // 删除关系
            examineeDocumentMapper.delete(examineeDocumentDOLambdaQueryWrapper);
        }
        // 保存资料
        List<DocumentDO> documentList = docFileList.stream()
                .map(item -> {
                    DocumentDO documentDO = new DocumentDO();

                    documentDO.setDocPath(String.join(",", item.getUrls()));
                    documentDO.setTypeId(item.getTypeId());

                    return documentDO;
                })
                .collect(Collectors.toList());

        documentMapper.insertBatch(documentList);

        // 保存关系
        List<ExamineeDocumentDO> relationList = documentList.stream()
                .map(item -> {

                    ExamineeDocumentDO relation = new ExamineeDocumentDO();

                    relation.setExamineeId(userId);
                    relation.setDocumentId(item.getId());
                    relation.setNoticeId(noticeId);

                    return relation;

                }).collect(Collectors.toList());

        examineeDocumentMapper.insertBatch(relationList);

        // 添加考生与报考通知关系
        LambdaQueryWrapper<ExamineeNoticeApplyDO> examineeNoticeApplyDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examineeNoticeApplyDOLambdaQueryWrapper.eq(ExamineeNoticeApplyDO::getExamineeId, userId)
                .eq(ExamineeNoticeApplyDO::getNoticeId, noticeId);
        List<ExamineeNoticeApplyDO> examineeNoticeApplyDOS =
                examineeNoticeApplyMapper.selectList(examineeNoticeApplyDOLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(examineeNoticeApplyDOS)) {
            ExamineeNoticeApplyDO examineeNoticeApplyDO = new ExamineeNoticeApplyDO();
            examineeNoticeApplyDO.setNoticeId(noticeId);
            examineeNoticeApplyDO.setExamineeId(userId);
            examineeNoticeApplyMapper.insert(examineeNoticeApplyDO);
        }

        return Boolean.TRUE;
    }


    /**
     * 撤销报名
     *
     * @param noticeId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelApply(Long noticeId) {
        Long userId = TokenLocalThreadUtil.get().getUserId();

        UserDO userDO = userMapper.selectById(userId);

        ValidationUtils.throwIfNull(userDO, "未登录");

        ExamineeNoticeApplyDO examineeNoticeApplyDO = examineeNoticeApplyMapper.selectOne(new LambdaQueryWrapper<ExamineeNoticeApplyDO>()
                .eq(ExamineeNoticeApplyDO::getNoticeId, noticeId)
                .eq(ExamineeNoticeApplyDO::getExamineeId, userId));
        ValidationUtils.throwIfNull(examineeNoticeApplyDO, "您未报考该通知");

        Long applyId = examineeNoticeApplyDO.getId();
        ExamineeNoticeApplyDO update = new ExamineeNoticeApplyDO();
        update.setId(applyId);
        update.setStatus(ExamineeNoticeApplyStatusEnum.PENDING_APPLY.getValue());
        update.setRemark("");
        examineeNoticeApplyMapper.updateById(update);
        // 删除报考项目
        examineeNoticeApplyRecordMapper.delete(new LambdaQueryWrapper<ExamineeNoticeApplyRecordDO>()
                .eq(ExamineeNoticeApplyRecordDO::getApplyId, applyId));
        return Boolean.TRUE;
    }

    /**
     * 构建级别节点
     */
    private List<CategoryNoticeTreeVO> buildLevelChildren(List<CategoryNoticeTreeDTO> categoryList) {

        // 按级别分组
        Map<Integer, List<CategoryNoticeTreeDTO>> levelMap = categoryList.stream()
                .filter(item -> item.getExamLevel() != null)
                .collect(Collectors.groupingBy(CategoryNoticeTreeDTO::getExamLevel));

        List<CategoryNoticeTreeVO> levelChildren = new ArrayList<>();

        for (Map.Entry<Integer, List<CategoryNoticeTreeDTO>> levelEntry : levelMap.entrySet()) {

            Integer examLevel = levelEntry.getKey();

            List<CategoryNoticeTreeDTO> noticeList = levelEntry.getValue();

            // 级别节点
            CategoryNoticeTreeVO levelNode = new CategoryNoticeTreeVO();
            levelNode.setValue(Long.valueOf(examLevel));
            levelNode.setLabel(getLevelName(examLevel));

            List<CategoryNoticeTreeVO> noticeChildren = noticeList.stream()
                    .map(notice -> {
                        CategoryNoticeTreeVO noticeNode = new CategoryNoticeTreeVO();
                        noticeNode.setValue(notice.getNoticeId());
                        noticeNode.setLabel(notice.getTitle());
                        return noticeNode;
                    })
                    .toList();

            levelNode.setChildren(noticeChildren);

            levelChildren.add(levelNode);
        }

        return levelChildren;
    }

    private String getLevelName(Integer level) {
        return switch (level) {
            case 1 -> "Ⅰ级";
            case 2 -> "Ⅱ级";
            default -> "未知级别";
        };
    }
}