package top.continew.admin.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.EnrollStatusConstant;
import top.continew.admin.common.constant.enums.EnrollExamStatusEnum;
import top.continew.admin.common.constant.enums.ExamineeNoticeApplyStatusEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.config.InspectorConfig;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.CandidateOfPlanAndRoomDTO;
import top.continew.admin.exam.model.dto.NoticePlanInfoDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyAuditReq;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyReq;
import top.continew.admin.exam.service.ExamineeNoticeApplyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 考生资料关系业务实现
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Service
@RequiredArgsConstructor
public class ExamineeNoticeApplyServiceImpl extends BaseServiceImpl<ExamineeNoticeApplyMapper, ExamineeNoticeApplyDO, ExamineeNoticeApplyResp, ExamineeNoticeApplyDetailResp, ExamineeNoticeApplyQuery, ExamineeNoticeApplyReq> implements ExamineeNoticeApplyService {

    private final ExamNoticePlanMapper examNoticePlanMapper;

    private final AESWithHMAC aesWithHMAC;

    private final ExamNoticeMapper examNoticeMapper;

    private final ExamIdcardMapper examIdcardMapper;

    private final UserMapper userMapper;

    private final ExamineeNoticeApplyRecordMapper examineeNoticeApplyRecordMapper;

    private final LicenseHolderInfoMapper licenseHolderInfoMapper;

    private final EnrollMapper enrollMapper;

    private final InspectorConfig inspectorConfig;

    private static final DateTimeFormatter YY_FORMATTER =
            DateTimeFormatter.ofPattern("yy");

    private static final DateTimeFormatter MM_FORMATTER =
            DateTimeFormatter.ofPattern("MM");


    /**
     * 获取通知对应的考生报名列表
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamineeNoticeApplyResp> getNoticeApplyCandidatePage(ExamineeNoticeApplyQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamineeNoticeApplyDO> queryWrapper = this.buildQueryWrapper(query);
        String username = query.getUsername();
        queryWrapper.eq("tena.is_deleted", 0)
                .eq(ObjectUtil.isNotNull(username), "su.username", aesWithHMAC.encryptAndSign(username));
        super.sort(queryWrapper, pageQuery);

        IPage<ExamineeNoticeApplyDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        List<ExamineeNoticeApplyDetailResp> records = page.getRecords();

        if (ObjectUtil.isNotEmpty(records)) {

            // 通知下所有项目
            List<NoticeExamProjectResp> noticeExamProjectRespList =
                    examNoticePlanMapper.selectNoticeExamProject(query.getNoticeId());

            records.forEach(item -> {

                // JSON转对象
                String json = item.getProjectExamListJson();

                List<ProjectExamResp> projectExamList;

                if (StrUtil.isNotBlank(json)) {

                    projectExamList = JSON.parseArray(
                            json,
                            ProjectExamResp.class
                    );

                } else {

                    projectExamList = Collections.emptyList();
                }

                item.setProjectExamList(projectExamList);

                // 当前考生已报考项目
                Map<Long, ProjectExamResp> projectMap =
                        projectExamList.stream()
                                .collect(Collectors.toMap(
                                        ProjectExamResp::getProjectId,
                                        Function.identity(),
                                        (a, b) -> a
                                ));


                // 组装返回给前端的项目列表
                List<NoticeExamProjectResp> noticeProjectList =
                        noticeExamProjectRespList.stream()
                                .map(project -> {

                                    NoticeExamProjectResp resp =
                                            new NoticeExamProjectResp();

                                    BeanUtils.copyProperties(project, resp);

                                    // 当前项目是否报考
                                    ProjectExamResp applyProject =
                                            projectMap.get(project.getProjectId());

                                    if (applyProject != null) {

                                        resp.setIsApply(Boolean.TRUE);

                                        resp.setExamAttemptType(
                                                applyProject.getExamAttemptType()
                                        );

                                    }
                                    return resp;
                                })
                                .toList();

                item.setNoticeExamProjectList(noticeProjectList);
                item.setUsername(aesWithHMAC.verifyAndDecrypt(item.getUsername()));
            });
        }

        PageResp<ExamineeNoticeApplyResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 获取考生报考详情
     *
     * @param applyId
     * @return
     */
    @Override
    public CandidateApplyDetailResp getCandidateApplyDetail(Integer applyId) {
        CandidateApplyDetailResp resp = new CandidateApplyDetailResp();
        // 查询报考信息是否存在
        ExamineeNoticeApplyDO examineeNoticeApplyDO = baseMapper.selectById(applyId);
        ValidationUtils.throwIfNull(examineeNoticeApplyDO, "考生报考信息不存在");
        Long noticeId = examineeNoticeApplyDO.getNoticeId();
        ExamNoticeDO examNoticeDO = examNoticeMapper.selectById(noticeId);
        ValidationUtils.throwIfNull(examNoticeDO, "考生所报考通知不存在");
        ExamineeNoticeApplyResp examineeNoticeApplyResp = new ExamineeNoticeApplyResp();
        BeanUtils.copyProperties(examineeNoticeApplyDO, examineeNoticeApplyResp);
        resp.setExamineeNoticeApplyResp(examineeNoticeApplyResp);
        // 查询考生的基本信息
        Long examineeId = examineeNoticeApplyDO.getExamineeId();
        UserDO userDO = userMapper.selectById(examineeId);
        ValidationUtils.throwIfNull(userDO, "考生信息不存在");
        ExamIdcardDO examIdcardDO = examIdcardMapper.selectOne(new LambdaQueryWrapper<ExamIdcardDO>()
                .eq(ExamIdcardDO::getIdCardNumber, userDO.getUsername()));
        ValidationUtils.throwIfNull(examNoticeDO, "考生基本信息不存在");
        ExamIdcardResp examIdcardResp = buildExamIdCardResp(examIdcardDO, userDO);
        resp.setExamIdcardResp(examIdcardResp);

        // 查询考生报考项目
        List<CandidateApplyProjectResp> applyProjectList = baseMapper.examineeNoticeApplyRecordMapper(applyId);
        ValidationUtils.throwIfEmpty(applyProjectList, "考生未报考项目");
        resp.setApplyProjectList(applyProjectList);

        // 查询考生持证信息
        List<LicenseHolderInfoDO> licenseHolderInfoDOS = licenseHolderInfoMapper.selectList(new LambdaQueryWrapper<LicenseHolderInfoDO>()
                .eq(LicenseHolderInfoDO::getExamineeId, examineeId));
        if (ObjectUtil.isNotEmpty(licenseHolderInfoDOS)) {
            resp.setLicenseHolderList(licenseHolderInfoDOS.stream().map(item -> {
                LicenseHolderInfoResp licenseHolderInfoResp = new LicenseHolderInfoResp();
                BeanUtils.copyProperties(item, licenseHolderInfoResp);
                return licenseHolderInfoResp;
            }).toList());
        }

        // 查询考生上传的资料列表
        Set<UploadedDocumentTypeVO> alreadyUploadDocSet =
                examNoticeMapper.getAlreadyUploadDocList(
                        examineeId,
                        examineeNoticeApplyDO.getNoticeId()
                );

        resp.setAlreadyUploadDocList(
                alreadyUploadDocSet == null
                        ? Collections.emptyList()
                        : alreadyUploadDocSet.stream()
                        .sorted(Comparator.comparing(UploadedDocumentTypeVO::getId))
                        .collect(Collectors.toList())
        );

        return resp;
    }

    /**
     * 审核
     *
     * @param applyAuditReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean audit(ExamineeNoticeApplyAuditReq applyAuditReq) {
        Long applyId = applyAuditReq.getId();
        ExamineeNoticeApplyDO examineeNoticeApplyDO = baseMapper.selectOne(new LambdaQueryWrapper<ExamineeNoticeApplyDO>()
                .eq(ExamineeNoticeApplyDO::getId, applyId));
        ValidationUtils.throwIfNull(examineeNoticeApplyDO, "考试报考信息不存在");
        Integer status = applyAuditReq.getStatus();
        ExamineeNoticeApplyDO update = new ExamineeNoticeApplyDO();
        update.setId(examineeNoticeApplyDO.getId());
        update.setStatus(status);
        update.setRemark(applyAuditReq.getRemark());

        // 不管审核通过还是不通过都先删掉考生对应的报考项目对应的计划信息,避免重复添加
        // 查出通知所绑定的计划
        List<NoticePlanInfoDTO> planInfos = examNoticeMapper.selectNoticePlanInfo(List.of(examineeNoticeApplyDO.getNoticeId()));
        ValidationUtils.throwIfEmpty(planInfos, "通知未绑定项目");
        List<Long> noticePlanIds = planInfos.stream().map(NoticePlanInfoDTO::getPlanId).toList();

        Long candidateId = examineeNoticeApplyDO.getExamineeId();
        enrollMapper.delete(new LambdaQueryWrapper<EnrollDO>()
                .eq(EnrollDO::getUserId, candidateId)
                .in(EnrollDO::getExamPlanId, noticePlanIds));

        // 如果通过添加准考证、计划信息
        if (ExamineeNoticeApplyStatusEnum.REVIEW_APPROVED.getValue().equals(status)) {
            // 查出考生分配到哪个计划，哪个考场
            List<CandidateOfPlanAndRoomDTO> candidateOfPlanAndRoomDTOS = baseMapper.selectCandidateOfPlanAndRoom(applyId);

            ValidationUtils.throwIfEmpty(candidateOfPlanAndRoomDTOS, "考生报考项目不存在");
            List<EnrollDO> enrollDOS = candidateOfPlanAndRoomDTOS.stream().map(item -> {
                EnrollDO enrollDO = new EnrollDO();
                enrollDO.setUserId(candidateId);
                enrollDO.setExamPlanId(item.getPlanId());
                enrollDO.setEnrollStatus(EnrollStatusConstant.SIGNED_UP);
                Long sort = item.getSort();
                enrollDO.setExamNumber(aesWithHMAC.encryptAndSign(generate(item.getProjectCode(), item.getStartTime(), item.getExamAttemptType(), sort)));
                enrollDO.setClassroomId(item.getClassroomId());
                enrollDO.setSeatId(sort);
                enrollDO.setExamStatus(EnrollExamStatusEnum.NOT_SIGNED.getValue());
                return enrollDO;
            }).toList();
            enrollMapper.insertBatch(enrollDOS);
        }
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 生成准考证号
     * <p>
     * 规则：
     * 260511001
     * <p>
     * 26 -> 年份
     * 05 -> 月份
     * 1  -> 项目类型（MT/PT/UT/RT）
     * 1  -> 初考/补考（1初考 2补考）
     * 001 -> 三位流水号
     */
    private String generate(String projectCode,
                            LocalDateTime examTime,
                            Integer examAttemptType,
                            Long sort) {

        Map<String, Integer> projectTypeMap =
                inspectorConfig.getProjectTypeMap();

        // 项目类型
        Integer projectType = projectTypeMap.get(projectCode);

        ValidationUtils.throwIfNull(
                projectType,
                "未知的项目编码：" + projectCode
        );

        // 年份后两位
        String year = YY_FORMATTER.format(examTime);

        // 月份
        String month = MM_FORMATTER.format(examTime);

        // 项目类型
        String project = String.valueOf(projectType);

        // 初考/补考
        String examType = String.valueOf(examAttemptType);

        // 三位流水号
        String number = String.format("%03d", sort);

        return year + month + project + examType + number;
    }

    private ExamIdcardResp buildExamIdCardResp(ExamIdcardDO examIdcardDO, UserDO userDO) {
        ExamIdcardResp examIdcardResp = new ExamIdcardResp();
        examIdcardResp.setRealName(examIdcardDO.getRealName());
        examIdcardResp.setGender(examIdcardDO.getGender());
        examIdcardResp.setIdCardNumber(aesWithHMAC.verifyAndDecrypt(examIdcardDO.getIdCardNumber()));
        examIdcardResp.setEducation(examIdcardDO.getEducation());
        examIdcardResp.setMajorType(examIdcardDO.getMajorType());
        examIdcardResp.setRelatedMajor(examIdcardDO.getRelatedMajor());
        examIdcardResp.setWorkYears(examIdcardDO.getWorkYears());
        examIdcardResp.setPhone(aesWithHMAC.verifyAndDecrypt(userDO.getPhone()));
        examIdcardResp.setCompanyName(examIdcardDO.getCompanyName());
        examIdcardResp.setAddress(examIdcardDO.getAddress());
        examIdcardResp.setRegion(examIdcardDO.getRegion());
        examIdcardResp.setQualification(examIdcardDO.getQualification());
        examIdcardResp.setPostalCode(examIdcardDO.getPostalCode());
        return examIdcardResp;
    }
}