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

import cn.crane4j.core.util.StringUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdcardUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.result.R;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.constant.EnrollStatusConstant;
import top.continew.admin.common.enums.SpecialCertificationApplicantEnum;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;

import top.continew.admin.config.ali.AliYunConfig;
import top.continew.admin.constant.SmsConstants;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.BatchAuditSpecialCertificationApplicantDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.EnrollReq;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantListReq;
import top.continew.admin.exam.model.resp.EnrollDetailResp;
import top.continew.admin.exam.service.EnrollService;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.SpecialCertificationApplicantQuery;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantReq;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantDetailResp;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantResp;
import top.continew.admin.exam.service.SpecialCertificationApplicantService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 特种设备人员资格申请业务实现
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SpecialCertificationApplicantServiceImpl extends BaseServiceImpl<SpecialCertificationApplicantMapper, SpecialCertificationApplicantDO, SpecialCertificationApplicantResp, SpecialCertificationApplicantDetailResp, SpecialCertificationApplicantQuery, SpecialCertificationApplicantReq> implements SpecialCertificationApplicantService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EnrollService enrollService;

    @Resource
    private AsyncClient smsAsyncClient;

    @Autowired
    private AliYunConfig aliYunConfig;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private SpecialCertificationApplicantMapper scMapper;

    @Resource
    private EnrollMapper enrollMapper;

    @Resource
    private ExamineePaymentAuditService examineePaymentAuditService;

    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    private final AESWithHMAC aesWithHMAC;

    private final LicenseCertificateMapper licenseCertificateMapper;

    /**
     * 根据考生和计划ID查询申报记录
     *
     * @param planId      计划ID
     * @param applySource 申报来源（0机构 / 1个人 / null不区分）
     */
    @Override
    public SpecialCertificationApplicantResp getByCandidates(Long planId, Integer applySource) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long candidateId = userTokenDo.getUserId();

        LambdaQueryWrapper<SpecialCertificationApplicantDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpecialCertificationApplicantDO::getCandidatesId, candidateId)
                .eq(SpecialCertificationApplicantDO::getPlanId, planId)
                .eq(SpecialCertificationApplicantDO::getIsDeleted, false)
                .eq(SpecialCertificationApplicantDO::getUpdateUser, candidateId)
                .eq(applySource != null, SpecialCertificationApplicantDO::getApplySource, applySource)
                .orderByDesc(SpecialCertificationApplicantDO::getCreateTime)
                .last("LIMIT 1");

        SpecialCertificationApplicantDO applicantDO = baseMapper.selectOne(queryWrapper);
        if (applicantDO == null) {
            return null;
        }

        SpecialCertificationApplicantResp resp = new SpecialCertificationApplicantResp();
        BeanUtils.copyProperties(applicantDO, resp);
        // 查找缴费记录
        ExamineePaymentAuditDO examineePaymentAuditDO = examineePaymentAuditMapper
                .selectOne(new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .eq(ExamineePaymentAuditDO::getExamPlanId, planId)
                        .eq(ExamineePaymentAuditDO::getExamineeId, candidateId)
                        .eq(ExamineePaymentAuditDO::getIsDeleted, false)
                        .last("LIMIT 1"));
        // 空值判断：为null时赋值null，不为null则取数据库值
        resp.setAuditStatus(examineePaymentAuditDO != null ? examineePaymentAuditDO.getAuditStatus() : null);
        resp.setRejectReason(examineePaymentAuditDO != null ? examineePaymentAuditDO.getRejectReason() : null);
        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean candidatesUpload(SpecialCertificationApplicantReq req) {

        UserTokenDo user = TokenLocalThreadUtil.get();
        Long userId = user.getUserId();
        LocalDate today = LocalDate.now();

        /* ================= 1. 查询考试计划 & 项目 ================= */
        ExamPlanDO examPlan = examPlanMapper.selectById(req.getPlanId());
        ValidationUtils.throwIf(examPlan == null, "考试计划不存在");

        ValidationUtils.throwIf(
                examPlan.getIsFinalConfirmed() == 2,
                "该考试计划已确定考试时间以及地点，无法报名。"
        );

        Long currentProjectId = examPlan.getExamProjectId();
        ValidationUtils.throwIf(currentProjectId == null, "考试项目不存在");

        ProjectDO project = projectMapper.selectById(currentProjectId);
        ValidationUtils.throwIf(project == null, "考试项目不存在");


        /* ================= 2. 项目唯一报名校验（核心） ================= */
        List<Long> publishedPlanIds = examPlanMapper.selectList(
                new LambdaQueryWrapper<ExamPlanDO>()
                        .eq(ExamPlanDO::getExamProjectId, currentProjectId)
                        .eq(ExamPlanDO::getStatus, 3) // 已发布生效
                        .eq(ExamPlanDO::getIsDeleted, false)
        ).stream().map(ExamPlanDO::getId).toList();

        if (!publishedPlanIds.isEmpty()) {
            boolean alreadyApplied =
                    baseMapper.exists(
                            new LambdaQueryWrapper<SpecialCertificationApplicantDO>()
                                    .eq(SpecialCertificationApplicantDO::getCandidatesId, userId)
                                    .in(SpecialCertificationApplicantDO::getPlanId, publishedPlanIds)
                                    // 排除当前计划
                                    .ne(SpecialCertificationApplicantDO::getPlanId, req.getPlanId())
                                    .eq(SpecialCertificationApplicantDO::getIsDeleted, false)
                    );

            ValidationUtils.throwIf(
                    alreadyApplied,
                    "您已在该考试项目下报名过其他已发布的考试计划，无法重复报名。"
            );
        }

        /* ================= 3. 证书校验（仅用于是否已取证） ================= */
        List<LicenseCertificateDO> certificates =
                licenseCertificateMapper.selectList(
                        new LambdaQueryWrapper<LicenseCertificateDO>()
                                .eq(LicenseCertificateDO::getCandidateId, userId)
                                .eq(LicenseCertificateDO::getIsDeleted, false)
                                .select(
                                        LicenseCertificateDO::getRecordId,
                                        LicenseCertificateDO::getEndDate
                                )
                );

        if (!certificates.isEmpty()) {

            // 1. 取所有证书关联的考试计划ID
            List<Long> recordIds = certificates.stream()
                    .map(LicenseCertificateDO::getRecordId)
                    .filter(Objects::nonNull)
                    .toList();

            if (!recordIds.isEmpty()) {

                // 2. 找出“当前考试项目”下的考试计划ID
                Set<Long> currentProjectPlanIds =
                        examPlanMapper.selectList(
                                        new LambdaQueryWrapper<ExamPlanDO>()
                                                .in(ExamPlanDO::getId, recordIds)
                                                .eq(ExamPlanDO::getExamProjectId, currentProjectId)
                                                .select(ExamPlanDO::getId)
                                ).stream()
                                .map(ExamPlanDO::getId)
                                .collect(Collectors.toSet());

                // 3. 只判断【当前项目】的证书是否存在未过期的
                boolean hasValidCurrentProjectCert =
                        certificates.stream()
                                .filter(c -> currentProjectPlanIds.contains(c.getRecordId()))
                                .anyMatch(c ->
                                        c.getEndDate() != null &&
                                                !c.getEndDate().isBefore(today)
                                );

                ValidationUtils.throwIf(
                        hasValidCurrentProjectCert,
                        "您已持有该考试项目的有效证书，无法再次报名。"
                );
            }
        }


        /* ================= 4. 身份证解密 & 年龄校验 ================= */
        String encryptIdCard = user.getUsername();
        ValidationUtils.throwIf(
                encryptIdCard == null || encryptIdCard.trim().isEmpty(),
                "身份证号为空，无法判断年龄"
        );

        String idCard;
        try {
            idCard = aesWithHMAC.verifyAndDecrypt(encryptIdCard);
        } catch (Exception e) {
            throw new BusinessException("身份证号解密失败");
        }

        LocalDate birthday = parseBirthdayFromIdCard(idCard);
        ValidationUtils.throwIf(birthday == null, "身份证号不合法，无法解析出生日期");

        int age = Period.between(birthday, today).getYears();

        if (project.getIsTheory() == 1) {
            ValidationUtils.throwIf(age > 60, "取证考试报名年龄不能超过60周岁");
        } else if (project.getIsTheory() == 0 && project.getProjectLevel() == 0) {
            ValidationUtils.throwIf(age > 60, "检验人员换证考试报名年龄不能超过60周岁");
        } else {
            ValidationUtils.throwIf(age > 65, "无损检测换证考试报名年龄不能超过65周岁");
        }


        /* ================= 5. 虚假资料校验 ================= */
        SpecialCertificationApplicantDO existingApplicant =
                baseMapper.selectOne(
                        new LambdaQueryWrapper<SpecialCertificationApplicantDO>()
                                .eq(SpecialCertificationApplicantDO::getCandidatesId, userId)
                                .eq(SpecialCertificationApplicantDO::getPlanId, req.getPlanId())
                                .eq(SpecialCertificationApplicantDO::getApplySource, 1)
                                .eq(SpecialCertificationApplicantDO::getIsDeleted, false)
                                .last("LIMIT 1")
                );

        ValidationUtils.throwIf(
                existingApplicant != null && existingApplicant.getStatus() == 3,
                "您的申报被标记为虚假资料，禁止再次申报该考试。如有疑问，请联系管理员。"
        );


        /* ================= 6. 距离考试不足 5 天 ================= */
        EnrollDetailResp detail =
                enrollMapper.getAllDetailEnrollList(req.getPlanId(), userId);

        ValidationUtils.throwIf(
                detail == null || detail.getExamStartTime() == null,
                "未找到考试计划或考试时间信息，无法上传资料。"
        );

        ValidationUtils.throwIf(
                LocalDateTime.now().isAfter(detail.getExamStartTime().minusDays(5)),
                "距离考试不足5天，无法重新上传资料。"
        );


        /* ================= 7. 保存 / 更新申报信息 ================= */
        if (existingApplicant != null &&
                (existingApplicant.getStatus() == 0 || existingApplicant.getStatus() == 2)) {

            existingApplicant.setImageUrl(req.getImageUrl());
            existingApplicant.setStatus(4);
            existingApplicant.setUpdateUser(userId);
            existingApplicant.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(existingApplicant);

        } else {
            SpecialCertificationApplicantDO entity = new SpecialCertificationApplicantDO();
            BeanUtils.copyProperties(req, entity);
            entity.setCandidatesId(userId);
            entity.setApplySource(1);
            entity.setStatus(0);
            entity.setIsDeleted(false);
            entity.setCreateUser(userId);
            entity.setUpdateUser(userId);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            baseMapper.insert(entity);
        }


        /* ================= 8. 同步报名表 ================= */
        EnrollDO enroll =
                enrollMapper.selectOne(
                        new LambdaQueryWrapper<EnrollDO>()
                                .eq(EnrollDO::getExamPlanId, req.getPlanId())
                                .eq(EnrollDO::getUserId, userId)
                                .eq(EnrollDO::getIsDeleted, false)
                                .last("LIMIT 1")
                );

        if (enroll == null) {
            enroll = new EnrollDO();
            enroll.setExamPlanId(req.getPlanId());
            enroll.setUserId(userId);
            enroll.setEnrollStatus(EnrollStatusConstant.UNDER_REVIEW);
            enroll.setIsDeleted(false);
            enroll.setCreateUser(userId);
            enroll.setUpdateUser(userId);
            enroll.setCreateTime(LocalDateTime.now());
            enroll.setUpdateTime(LocalDateTime.now());
            enrollMapper.insert(enroll);
        } else {
            enroll.setEnrollStatus(EnrollStatusConstant.UNDER_REVIEW);
            enroll.setUpdateTime(LocalDateTime.now());
            enrollMapper.updateById(enroll);
        }

        return true;
    }
    private LocalDate parseBirthdayFromIdCard(String idCard) {
        try {
            String birth;
            if (idCard.length() == 18) {
                birth = idCard.substring(6, 14);
            } else if (idCard.length() == 15) {
                birth = "19" + idCard.substring(6, 12);
            } else {
                return null;
            }
            return LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean candidatesUploads(SpecialCertificationApplicantListReq scar) {
        Map<String, String> studentMap;
        List<SpecialCertificationApplicantDO> scaList = new ArrayList<>();
        try {
            ObjectMapper om = new ObjectMapper();
            studentMap = om.readValue(scar.getStudentMapStr(), Map.class);
            ValidationUtils.throwIfNull(studentMap, "请上传申请表");
            scaList = getSpecialCertificationApplicantDOS(scar, studentMap);
        } catch (Exception e) {
            log.error("错误", e);
        }
        List<UserDO> userDO = scMapper.selectLog(scaList, scar.getPlanId(), SpecialCertificationApplicantEnum.UNAUDITED
                .getValue());
        for (UserDO u : userDO) {
            ValidationUtils.throwIfNotNull(userDO, "申请提交失败，" + u.getNickname() + " 已提交申请表，请等待审批");
        }
        return scMapper.insertStudentImage(scaList, TokenLocalThreadUtil.get().getUserId()) != 0;
    }

    private static @NotNull List<SpecialCertificationApplicantDO> getSpecialCertificationApplicantDOS(SpecialCertificationApplicantListReq scar,
                                                                                                      Map<String, String> studentMap) {
        List<SpecialCertificationApplicantDO> scaList = new ArrayList<>();
        for (Map.Entry<String, String> entry : studentMap.entrySet()) {
            SpecialCertificationApplicantDO scaDB = new SpecialCertificationApplicantDO();
            scaDB.setCandidatesId(Long.parseLong(entry.getKey()));
            scaDB.setImageUrl(entry.getValue());
            scaDB.setPlanId(Long.parseLong(scar.getPlanId()));
            scaDB.setStatus(SpecialCertificationApplicantEnum.UNAUDITED.getValue());
            scaList.add(scaDB);
        }
        return scaList;
    }

    /**
     * 批量审核
     *
     * @param dto
     */
    @Override
    //TODO 优化批量审核
    public R batchAudit(BatchAuditSpecialCertificationApplicantDTO dto) {
        //1.从dto获取ids和status
        //2.判断成功还是失败
        EnrollReq enrollReq = new EnrollReq();
        enrollReq.setEnrollStatus(1L);
        boolean success = false;
        if (dto.getStatus() == 2)//不通过
        {
            //2.1修改状态
            LambdaUpdateWrapper<SpecialCertificationApplicantDO> specialCertificationApplicantDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            specialCertificationApplicantDOLambdaUpdateWrapper.in(SpecialCertificationApplicantDO::getId, dto.getIds())
                    .set(SpecialCertificationApplicantDO::getStatus, dto.getStatus());
            super.update(specialCertificationApplicantDOLambdaUpdateWrapper);

            //2.2先发送短信，循环发送
            List<String> ids = dto.getIds();
            QueryWrapper<SpecialCertificationApplicantDO> idWrapper = new QueryWrapper<SpecialCertificationApplicantDO>()
                    .in("id", ids);
            List<SpecialCertificationApplicantDO> scaList = baseMapper.selectList(idWrapper);
            for (SpecialCertificationApplicantDO sca : scaList) {
                // 2.1.1构建计划名称
                ExamPlanDO examPlanDO = examPlanMapper.selectById(sca.getPlanId());
                String examPlanName = examPlanDO.getExamPlanName();
                //  2.1.2构建手机号
                String phone = userMapper.selectById(sca.getCandidatesId()).getPhone();
                sms(examPlanName, phone, "审核不通过，原因是:" + dto.getReason());
            }
        } else if (dto.getStatus() == 1) {

            List<String> ids = dto.getIds();
            QueryWrapper<SpecialCertificationApplicantDO> idWrapper = new QueryWrapper<SpecialCertificationApplicantDO>()
                    .in("id", ids);
            List<SpecialCertificationApplicantDO> scaList = baseMapper.selectList(idWrapper);
            //1.检查有没有达到最大人数循环检查
            for (SpecialCertificationApplicantDO sca : scaList) {
                // 2.1.1构建计划名称
                ExamPlanDO examPlanDO = examPlanMapper.selectById(sca.getPlanId());
                String examPlanName = examPlanDO.getExamPlanName();
                enrollReq.setExamPlanId(sca.getPlanId());
                //  2.1.2构建手机号
                String phone = userMapper.selectById(sca.getCandidatesId()).getPhone();
                ExamPlanDO examPlanDO1 = examPlanMapper.selectById(sca.getPlanId());//获取计划信息，提取最大人数
                long enrolledCount = classroomMapper.getPlanCount(sca.getPlanId());//获取当前计划已经报名人数

                if (enrolledCount >= examPlanDO1.getMaxCandidates()) {

                    String reason = "审核不通过,原因是:该考试计划报名人数已满!";
                    //发送短信+赋值失败
                    //检验是否还需要跟新状态
                    LambdaQueryWrapper<SpecialCertificationApplicantDO> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SpecialCertificationApplicantDO::getStatus, 0)
                            .eq(SpecialCertificationApplicantDO::getPlanId, sca.getPlanId());
                    if (baseMapper.selectCount(wrapper) > 0) {
                        success = true;
                        smsList(examPlanName, String.valueOf(sca.getPlanId()), reason);
                        batchRejectApplications(sca.getPlanId());
                    }
                } else {
                    //2.插入报名表
                    if (enrollService.signUp(enrollReq, sca.getCandidatesId(), sca.getStatus())) {
                        //3.修改状态
                        SpecialCertificationApplicantReq specialCertificationApplicantReq = new SpecialCertificationApplicantReq();
                        BeanUtils.copyProperties(sca, specialCertificationApplicantReq);
                        specialCertificationApplicantReq.setStatus(dto.getStatus());
                        super.update(specialCertificationApplicantReq, sca.getId());
                        //4.发送短信
                        sms(examPlanName, phone, "已经审核通过!");
                    }
                }

            }
        }
        if (success)
            return R.status(false, "批量审核成功，部分考试计划报名人数已满，已自动改为审核不通过!");
        return R.status(true, "批量审核成功");
    }

    @Override
    //重写page
    public PageResp<SpecialCertificationApplicantResp> page(SpecialCertificationApplicantQuery query,
                                                            PageQuery pageQuery) {
        //根据mapper查出考生名
        //封装返回结果
        QueryWrapper<SpecialCertificationApplicantDO> queryWrapper = this.buildQueryWrapper(query);
        if (query.getCandidatesName() != null) {
            queryWrapper.like("su.nickname", query.getCandidatesName());
        }
        if (query.getExamPlanName() != null) {
            queryWrapper.like("tep.exam_plan_name", query.getExamPlanName());
        }

        queryWrapper.eq("tsca.is_deleted", 0);
        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);
        // 执行分页查询
        IPage<SpecialCertificationApplicantResp> page = baseMapper.getSpecialCertification(new Page<>(pageQuery
                .getPage(), pageQuery.getSize()), queryWrapper);
        // 将查询结果转换成 PageResp 对象
        PageResp<SpecialCertificationApplicantResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    /**
     * @param req 修改参数
     * @param id  ID
     */

    @Override
    public R updateResult(SpecialCertificationApplicantReq req, Long id) throws Exception {
        SpecialCertificationApplicantDO applicantDO = baseMapper.selectById(id);
        EnrollReq enrollReq = new EnrollReq();
        enrollReq.setEnrollStatus(1L);
        enrollReq.setExamPlanId(applicantDO.getPlanId());

        UserDO userDO = userMapper.selectById(applicantDO.getCandidatesId());
        String phone = userDO.getPhone();

        ExamPlanDO examPlanDO = examPlanMapper.selectById(applicantDO.getPlanId());
        String examPlanName = examPlanDO.getExamPlanName();

        Integer status = req.getStatus();

        // === 审核通过 ===
        if (status == 1) {
            long enrolledCount = classroomMapper.getPlanCount(req.getPlanId());
            if (enrolledCount >= examPlanDO.getMaxCandidates()) {
                String reason = "审核不通过，原因是：该考试计划报名人数已满！";
                smsList(examPlanName, String.valueOf(req.getPlanId()), reason);
                batchRejectApplications(req.getPlanId());
                return R.status(false, "该考试计划报名人数已满，已自动改为审核不通过！");
            }
            super.update(req, id);
            // 审核通过 -> 报名状态改为已报名
            enrollMapper.updateEnrollStatus(applicantDO.getPlanId(), applicantDO.getCandidatesId(), 1L);
            // 发送短信
            sms(examPlanName, phone, "审核通过，报名成功！");
            //创建缴费审核表记录
            EnrollDO enrollDO = enrollMapper.selectOne(new LambdaQueryWrapper<EnrollDO>()
                    .eq(EnrollDO::getExamPlanId, applicantDO.getPlanId())
                    .eq(EnrollDO::getUserId, applicantDO.getCandidatesId())
                    .select(EnrollDO::getId)
                    .last("LIMIT 1"));
            examineePaymentAuditService.generatePaymentAudit(applicantDO.getPlanId(), applicantDO
                    .getCandidatesId(), enrollDO.getId());
            return R.status(true, "报名成功");
        }

        // === 退回补正 ===
        else if (status == 2) {
            if (StringUtils.isBlank(req.getRemark())) {
                return R.status(false, "退回补正必须填写退回原因！");
            }

            super.update(req, id);
            // 审核退回 -> 报名状态改为待补正
            enrollMapper.updateEnrollStatus(applicantDO.getPlanId(), applicantDO.getCandidatesId(), 5L);
            sms(examPlanName, phone, "审核未通过，原因：" + req.getRemark());
            return R.status(true, "退回补正成功");
        }

        // === 虚假资料 ===
        else if (status == 3) {
            if (StringUtils.isBlank(req.getRemark())) {
                return R.status(false, "标记为虚假资料时必须填写原因！");
            }

            super.update(req, id);
            // 标记虚假资料 -> 报名状态改为禁止申报
            enrollMapper.updateEnrollStatus(applicantDO.getPlanId(), applicantDO.getCandidatesId(), 6L);
            sms(examPlanName, phone, "您的申报被标记为虚假资料，原因：" + req.getRemark() + "。您将无法再次申报该考试。");
            return R.status(true, "已标记为虚假资料并禁止再次申报");
        }
        // === 其他状态 ===
        else {
            super.update(req, id);
            return R.status(true, "审核状态已更新");
        }
    }

    public void batchRejectApplications(Long planId) {
        LambdaUpdateWrapper<SpecialCertificationApplicantDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SpecialCertificationApplicantDO::getStatus, 0)
                .eq(SpecialCertificationApplicantDO::getPlanId, planId)
                .set(SpecialCertificationApplicantDO::getStatus, 2);
        baseMapper.update(null, wrapper);
    }

    /**
     * 批量处理超限人数的短信
     *
     * @param examPlanName
     * @param planId
     * @param reason
     */
    public void smsList(String examPlanName, String planId, String reason) {
        //3.捕获调用的异常后，直接剩下的未审核的同学发送失败短信
        //3.1 通过这个查询条件，获取所有未审核的考生id
        LambdaQueryWrapper<SpecialCertificationApplicantDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpecialCertificationApplicantDO::getStatus, 0);
        queryWrapper.eq(SpecialCertificationApplicantDO::getPlanId, planId);

        List<SpecialCertificationApplicantDO> specialCertificationApplicantDOList = baseMapper.selectList(queryWrapper);
        List<Long> candidatesIds = specialCertificationApplicantDOList.stream().map(item -> {
            return item.getCandidatesId();
        }).collect(Collectors.toList());
        //3.2 获取手机号
        QueryWrapper wrapper = new QueryWrapper<UserDO>().in("id", candidatesIds);
        List<UserDO> userDOList = userMapper.selectListByIds(wrapper);
        List<String> phones = userDOList.stream().map(item -> item.getPhone()).collect(Collectors.toList());//获取他们手机号

        //3.3构建短信

        Map<String, String> templates = new LinkedHashMap<>();
        templates.put("name", examPlanName);
        templates.put("flag", reason);
        String params = JSON.toJSONString(templates);
        String phoneList = String.join(",", phones);
        log.info("手机号：{}", phoneList);

        SendSmsRequest request = SendSmsRequest.builder()
                .phoneNumbers(phoneList)//手机号
                .signName(aliYunConfig.getSignName())
                .templateCode(aliYunConfig.getTemplateCodes().get(SmsConstants.ENROLLMENT_CONFIRMATION_TEMPLATE))
                .templateParam(params)//验证码
                .build();
        CompletableFuture<SendSmsResponse> future = smsAsyncClient.sendSms(request);
        future.thenAccept(response -> {
            if ("OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功 | phone: {} | code: {}", phoneList, response.getBody().getCode());

            } else {
                log.error("短信发送失败 | phone: {} | code: {}", phoneList, response.getBody().getCode());
            }
        }).exceptionally(ex -> {
            log.error("短信发送异常 | phone: {}", phoneList, ex);
            return null;
        });
    }

    /**
     * 单个发送短信
     *
     * @param examPlanName
     * @param phone
     * @param reason
     */
    public void sms(String examPlanName, String phone, String reason) {
        //1.获取用户电话号码(默认是必须的)

        //3.3构建短信
        Map<String, String> templateParams = new LinkedHashMap<>();
        templateParams.put("name", examPlanName);
        templateParams.put("flag", reason);
        String params = JSON.toJSONString(templateParams);
        //2.2  构建短信
        SendSmsRequest request = SendSmsRequest.builder()
                .phoneNumbers(phone)//手机号
                .signName(aliYunConfig.getSignName())
                .templateCode(aliYunConfig.getTemplateCodes().get(SmsConstants.ENROLLMENT_CONFIRMATION_TEMPLATE))
                .templateParam(params)//验证码
                .build();
        //2.3异步发送
        CompletableFuture<SendSmsResponse> future = smsAsyncClient.sendSms(request);
        future.thenAccept(response -> {
            if ("OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功 | phone: {} | code: {}", phone, response.getBody().getCode());

            } else {
                log.error("短信发送失败 | phone: {} | code: {}", phone, response.getBody().getCode());
            }
        }).exceptionally(ex -> {
            log.error("短信发送异常 | phone: {}", phone, ex);
            return null;
        });

    }

    @Override
    //重写查询get
    public SpecialCertificationApplicantDetailResp get(Long id) {
        SpecialCertificationApplicantDO entity = super.getById(id, false);
        SpecialCertificationApplicantDetailResp detail = BeanUtil.toBean(entity, this.getDetailClass());
        detail.setBatchId(entity.getBatchId());
        detail.setRemark(entity.getRemark());
        detail.setCandidatesName(userMapper.selectNicknameById(entity.getCandidatesId()));
        this.fill(detail);
        return detail;
    }

}