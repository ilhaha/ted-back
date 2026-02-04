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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.utils.ObjectUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.auth.model.resp.ExamCandidateInfoVO;
import top.continew.admin.common.constant.*;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.DateUtil;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.ClassroomInvigilatorDTO;
import top.continew.admin.exam.model.dto.ExamPlanDTO;
import top.continew.admin.exam.model.dto.ExamPlanExcelRowDTO;
import top.continew.admin.exam.model.dto.ExamPresenceDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.*;
import top.continew.admin.exam.model.resp.CascaderOptionResp;
import top.continew.admin.exam.model.resp.CascaderPlanResp;
import top.continew.admin.exam.model.vo.InvigilateExamPlanVO;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.exam.service.ExamRecordsService;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.admin.examconnect.service.QuestionBankService;
import top.continew.admin.invigilate.mapper.LaborFeeMapper;
import top.continew.admin.invigilate.mapper.PlanInvigilateMapper;
import top.continew.admin.invigilate.model.entity.LaborFeeDO;
import top.continew.admin.invigilate.model.entity.PlanInvigilateDO;
import top.continew.admin.invigilate.model.enums.InvigilateStatus;
import top.continew.admin.invigilate.model.resp.AvailableInvigilatorResp;
import top.continew.admin.invigilate.model.resp.InvigilatorAssignResp;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.service.UserService;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.mapper.OrgMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.admin.worker.model.entity.WorkerExamTicketDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.model.query.ExamPlanQuery;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
import top.continew.admin.exam.service.ExamPlanService;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 考试计划业务实现
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Service
@RequiredArgsConstructor
public class ExamPlanServiceImpl extends BaseServiceImpl<ExamPlanMapper, ExamPlanDO, ExamPlanResp, ExamPlanDetailResp, ExamPlanQuery, ExamPlanReq> implements ExamPlanService {

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private PlanInvigilateMapper planInvigilateMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private PlanClassroomMapper planClassroomMapper;

    @Resource
    private SpecialCertificationApplicantMapper specialCertificationApplicantMapper;

    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    @Value("${examine.userRole.invigilatorId}")
    private Long invigilatorId;

    private final QuestionBankService questionBankService;

    private final EnrollMapper enrollMapper;

    private final CandidateTicketService candidateTicketReactiveService;

    private final AESWithHMAC aesWithHMAC;

    private final UserMapper userMapper;

    private final WorkerExamTicketMapper workerExamTicketMapper;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    private final OrgClassMapper orgClassMapper;

    private final LaborFeeMapper laborFeeMapper;

    private UserService userService;

    private final OrgMapper orgMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ExamRecordsMapper examRecordsMapper;

    private final WeldingOperScoreMapper weldingOperScoreMapper;

    private final WorkerApplyMapper workerApplyMapper;

    private final OrgClassCandidateMapper orgClassCandidateMapper;

    private final PlanApplyClassMapper planApplyClassMapper;

    private final ExamRecordsService examRecordsService;

    @Value("${certificate.road-exam-type-id}")
    private Long roadExamTypeId;

    /**
     * 特种设备相关管理分类 ID（理论考试）
     */
    @Value("${category.special-equipment-manage-type-id}")
    private Long specialEquipmentManageTypeId;

    @Value("${welding.metal-project-id}")
    private Long metalProjectId;

    @Value("${welding.nonmetal-project-id}")
    private Long nonmetalProjectId;

    @Override
    public PageResp<ExamPlanResp> page(ExamPlanQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        if (!ExamPlanStatusEnum.STARTED.getValue().equals(query.getStatus())) {
            queryWrapper.ne("tep.status", ExamPlanStatusEnum.STARTED);
        }
        super.sort(queryWrapper, pageQuery);

        IPage<ExamPlanDetailResp> page = baseMapper.selectExamPlanPage(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        page.getRecords().forEach(item -> item.setStatusString(PlanConstant.getStatusString(item.getStatus())));

        PageResp<ExamPlanResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    @Override
    public ExamPlanDetailResp get(Long id) {
        ExamPlanDetailResp examPlanDetailResp = baseMapper.selectDetailById(id);
        ValidationUtils.throwIfNull(examPlanDetailResp, "未查询到考试计划信息");
        // 查询计划对应的考场信息
        //        LambdaQueryWrapper<ExamPlanClassroomDO> examPlanClassroomDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //        examPlanClassroomDOLambdaQueryWrapper.eq(ExamPlanClassroomDO::getPlanId, id)
        //                .select(ExamPlanClassroomDO::getClassroomId,ExamPlanClassroomDO::getPlanId);
        //        List<ExamPlanClassroomDO> examPlanClassroomDOS = examPlanClassroomMapper
        //                .selectList(examPlanClassroomDOLambdaQueryWrapper);
        //        if (ObjectUtil.isNotEmpty(examPlanClassroomDOS)) {
        //            examPlanDetailResp.setClassroomId(examPlanClassroomDOS.stream()
        //                    .map(ExamPlanClassroomDO::getClassroomId)
        //                    .collect(Collectors.toList()));
        //        }
        this.fill(examPlanDetailResp);
        return examPlanDetailResp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ExamPlanSaveReq req) {

        // 禁止计划重名
        ExamPlanDO examPlanName = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>().eq("exam_plan_name", req
                .getExamPlanName()).eq("is_deleted", 0).eq("plan_type", req.getPlanType()));
        ValidationUtils.throwIfNotNull(examPlanName, "考试计划名称已存在");

        // 1. 构建对象
        ExamPlanDO examPlanDO = getExamPlanDO(req);

        // 2. 校验报名结束时间
        //        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(examPlanDO.getEnrollEndTime(), examPlanDO
        //                .getStartTime()), "报名结束时间不能晚于考试开始时间");

        // 3. 合并考场 ID（理论 + 实操）
        //        List<Long> classroomIds = new ArrayList<>();
        //        List<Long> theoryIds = req.getTheoryClassroomId();
        //        List<Long> operIds = req.getOperationClassroomId();
        //        Integer maxCandidates = req.getMaxCandidates();
        //
        //        if (!CollectionUtils.isEmpty(theoryIds)) {
        //            classroomIds.addAll(theoryIds);
        //        }
        //        if (!CollectionUtils.isEmpty(operIds)) {
        //            classroomIds.addAll(operIds);
        //        }
        //
        //        // 4. 判断考场是否冲突（为空则不会查数据库）
        //        if (!CollectionUtils.isEmpty(classroomIds)) {
        //            List<String> conflictClassrooms = baseMapper.listConflictClassrooms(req.getStartTime(), classroomIds);
        //            ValidationUtils.throwIfNotEmpty(
        //                    conflictClassrooms,
        //                    "以下考场当天已存在考试：" + String.join("、", conflictClassrooms)
        //            );
        //        }
        //
        //        // 5. 验证理论/实操考场容量（避免空列表导致 SQL 报错）
        //        long theoryCapacity = CollectionUtils.isEmpty(theoryIds)
        //                ? 0
        //                : classroomMapper.getMaxCandidates(theoryIds);
        //
        //        boolean hasOperCapacity = CollectionUtils.isEmpty(operIds);
        //        long operCapacity = hasOperCapacity
        //                ? 0
        //                : classroomMapper.getMaxCandidates(operIds);
        //
        //        ValidationUtils.throwIf(maxCandidates > theoryCapacity,
        //                "理论考场容量不足，无法容纳当前考试人数");
        //
        //        ValidationUtils.throwIf(!hasOperCapacity && maxCandidates > operCapacity,
        //                "实操考场容量不足，无法容纳当前考试人数");

        // 6. copy 属性（必须在容量判断之后）
        BeanUtils.copyProperties(req, examPlanDO);

        // 7. 设置最终状态
        examPlanDO.setIsFinalConfirmed(PlanFinalConfirmedStatus.ADMIN_PENDING.getValue());

        // 8. 保存计划
        this.save(examPlanDO);

        // 9. 保存考场关联
        //        if (!CollectionUtils.isEmpty(classroomIds)) {
        //            examPlanMapper.savePlanClassroom(examPlanDO.getId(), classroomIds);
        //        }
    }

    /**
     * 判读考试计划的考试时间对应的考场是否冲突
     *
     * @param startTime
     * @param endTime
     * @param classroomId
     * @return
     */
    private Integer hasClassroomTimeConflict(LocalDateTime startTime,
                                             LocalDateTime endTime,
                                             List<Long> classroomId,
                                             Long planId) {
        return baseMapper.hasClassroomTimeConflict(startTime, endTime, classroomId, planId);
    }

    /**
     * 时间转换
     */
    private ExamPlanDO getExamPlanDO(ExamPlanSaveReq examPlanSaveReq) {
        //        List<String> dateRange = examPlanSaveReq.getDateRange();
        List<String> enrollList = examPlanSaveReq.getEnrollList();
        ExamPlanDO examPlanDO = new ExamPlanDO();
        // 年份
        //        examPlanDO.setPlanYear(dateRange.get(0).substring(0, dateRange.get(0).indexOf("-")));
        examPlanDO.setPlanYear(String.valueOf(examPlanSaveReq.getStartTime().getYear()));
        // 报名时间
        examPlanDO.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        examPlanDO.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));
        // 考试结束时间
        //        examPlanDO.setStartTime(DateUtil.parse(dateRange.get(0)));
        //        examPlanDO.setEndTime(DateUtil.parse(dateRange.get(1)));
        ProjectDO projectDO = projectMapper.selectById(examPlanSaveReq.getExamProjectId());
        examPlanDO.setStartTime(examPlanSaveReq.getStartTime());
        //        examPlanDO.setEndTime(examPlanSaveReq.getStartTime().plusMinutes(projectDO.getExamDuration()));
        examPlanDO.setPlanType(projectDO.getProjectType());
        return examPlanDO;
    }

    @Override
    public String valid(Long examPlanId, Integer status) {

        ValidationUtils.throwIfNull(examPlanId, "考试计划字段为空");

        Long userId = TokenLocalThreadUtil.get().getUserId();

        QueryWrapper<ExamPlanDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", examPlanId);
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(queryWrapper);

        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        String approvedUsers = examPlanDO.getApprovedUsers();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        ExamPlanDO examPlanResp = new ExamPlanDO();
        BeanUtils.copyProperties(examPlanDO, examPlanResp);
        //        status == 0  未通过
        if (status == 0) {
            if (approvedUsers == null || approvedUsers.isEmpty()) {
                StringBuilder approveUser = new StringBuilder(userId.toString());
                examPlanResp.setApprovedUsers(approveUser.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.FAIL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划,主任未通过";
            } else {
                String existApproveUser = examPlanDO.getApprovedUsers();
                StringBuilder existApproveUserBuilder = new StringBuilder(existApproveUser);

                StringBuilder approveUser = new StringBuilder(userId.toString());
                existApproveUserBuilder.append(",").append(approveUser);

                examPlanResp.setApprovedUsers(existApproveUserBuilder.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.FAIL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划,市监局未通过";
            }
        }

        //        status == 1
        if (status == 1) {
            if (approvedUsers == null || approvedUsers.isEmpty()) {
                StringBuilder approveUser = new StringBuilder(userId.toString());
                examPlanResp.setApprovedUsers(approveUser.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.AUDIT_SUPERVISION_APPROVAL.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划主任通过,待市监局审核";
            } else {
                String existApproveUser = examPlanDO.getApprovedUsers();
                StringBuilder existApproveUserBuilder = new StringBuilder(existApproveUser);

                StringBuilder approveUser = new StringBuilder(userId.toString());
                existApproveUserBuilder.append(",").append(approveUser);

                examPlanResp.setApprovedUsers(existApproveUserBuilder.toString());
                examPlanResp.setApprovalTime(formattedDateTime);
                examPlanResp.setStatus(PlanConstant.SUCCESS.getStatus());
                examPlanMapper.updateById(examPlanResp);
                return "考试计划主任,市监局都通过";
            }

        }
        return "";
    }

    @Override
    public List<ExamPlanDO> getAllList() {
        return examPlanMapper.selectAllList();
    }

    /**
     * 获取考试计划考场
     *
     * @param planId
     * @return
     */
    @Override
    public List<Long> getPlanExamClassroom(Long planId) {
        return examPlanMapper.getPlanExamClassroom(planId);
    }

    /**
     * 修改考试计划考场
     *
     * @param planId
     * @param classroomId
     * @return
     */
    @Override
    public Boolean updatePlanExamClassroom(Long planId, List<Long> classroomId) {
        examPlanMapper.deletePLanExamClassroom(planId);
        examPlanMapper.savePlanClassroom(planId, classroomId);
        // 通过考场id获取最大人数
        Long maxCandidates = classroomMapper.getMaxCandidates(classroomId);
        // 修改考试计划最大人数
        ExamPlanDO examPlanDO = new ExamPlanDO();
        examPlanDO.setId(planId);
        examPlanDO.setMaxCandidates(Math.toIntExact(maxCandidates));
        examPlanMapper.updateById(examPlanDO);
        return true;
    }

    @Override
    public Integer selectClassroomId(Integer planId) {
        return examPlanMapper.selectClassroomById(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean endExam(Long planId) {

        // 1. 查询该计划所有报名信息
        LambdaQueryWrapper<EnrollDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnrollDO::getExamPlanId, planId);
        List<EnrollDO> enrollList = enrollMapper.selectList(wrapper);

        List<EnrollDO> signedUpList = enrollList.stream().filter(item -> {
            return item.getEnrollStatus().equals(EnrollStatusConstant.SIGNED_UP);
        }).toList();

        // 2. 检查是否有正在考试的考生
        Set<Integer> validStatuses = Set
                .of(EnrollStatusConstant.SIGNED_IN, EnrollStatusConstant.IN_PROGRESS, EnrollStatusConstant.RETAKE_IN_PROGRESS);

        List<Long> signedInList = signedUpList.stream()
                .filter(e -> validStatuses.contains(e.getExamStatus()))
                .map(EnrollDO::getId)
                .toList();
        ValidationUtils.throwIfNotEmpty(signedInList, "仍有考生正在考试，无法结束考试，请稍后再试");

        // 3. 找出未签到 → 改成缺勤（ABSENT）
        List<Long> notSignedInIds = signedUpList.stream()
                .filter(e -> EnrollStatusConstant.NOT_SIGNED_IN.equals(e.getExamStatus()))
                .map(EnrollDO::getId)
                .toList();

        if (!notSignedInIds.isEmpty()) {
            // 1. 更新报名状态
            enrollMapper.update(new LambdaUpdateWrapper<EnrollDO>().in(EnrollDO::getId, notSignedInIds)
                    .set(EnrollDO::getExamStatus, EnrollStatusConstant.ABSENT)
                    .set(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED));

            // 2. 查询是否有实操/道路考试
            ExamPresenceDTO examPlanOperAndRoadDTO = examRecordsMapper.hasOperationOrRoadExam(planId, roadExamTypeId);
            boolean hasOper = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsOperation());
            boolean hasRoad = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsRoad());

            // 3. 批量生成考试记录
            List<ExamRecordsDO> examRecordsList = signedUpList.stream().map(item -> {
                ExamRecordsDO examRecordsDO = new ExamRecordsDO();
                examRecordsDO.setPlanId(planId);
                examRecordsDO.setCandidateId(item.getUserId());
                examRecordsDO.setExamScores(0);
                examRecordsDO.setOperScores(hasOper ? 0 : ExamRecordConstants.PASSING_SCORE);
                examRecordsDO.setOperInputStatus(hasOper
                        ? ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                        : ExamScoreEntryStatusEnum.ENTERED.getValue());
                examRecordsDO.setRoadScores(0);
                examRecordsDO.setRoadInputStatus(hasRoad
                        ? ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                        : ExamScoreEntryStatusEnum.ENTERED.getValue());
                examRecordsDO.setAttemptType(ExamRecordAttemptEnum.FIRST.getValue());
                examRecordsDO.setExamResultStatus(ExamResultStatusEnum.FAILED.getValue());
                return examRecordsDO;
            }).toList();

            examRecordsMapper.insertBatch(examRecordsList);

            // 判断是否是焊接项目
            ExamPlanDO examPlanDO = examPlanMapper.selectById(planId);
            Long examProjectId = examPlanDO.getExamProjectId();
            if (metalProjectId.equals(examProjectId) || nonmetalProjectId.equals(examProjectId)) {
                // 4. 批量生成焊接实操成绩
                insertBatchWeldingOperScore(examRecordsList);
            }
        }

        // 4. 找出已交卷 → 改成已完成（COMPLETED）
        List<Long> submittedIds = signedUpList.stream()
                .filter(e -> EnrollStatusConstant.SUBMITTED.equals(e.getExamStatus()))
                .map(EnrollDO::getId)
                .toList();

        if (!submittedIds.isEmpty()) {
            enrollMapper.update(new LambdaUpdateWrapper<EnrollDO>().in(EnrollDO::getId, submittedIds)
                    .set(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED));
        }

        // 5. 更新考试计划状态 → 已结束
        LambdaUpdateWrapper<ExamPlanDO> planUpdate = new LambdaUpdateWrapper<>();
        planUpdate.eq(ExamPlanDO::getId, planId).set(ExamPlanDO::getStatus, PlanConstant.OVER.getStatus());
        this.update(planUpdate);

        // 6. 更新监考员状态 → 待审核
        LambdaUpdateWrapper<PlanInvigilateDO> invUpdate = new LambdaUpdateWrapper<>();
        invUpdate.eq(PlanInvigilateDO::getExamPlanId, planId)
                .eq(PlanInvigilateDO::getInvigilatorId, TokenLocalThreadUtil.get().getUserId())
                .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatus.COMPLETED.getCode());
        planInvigilateMapper.update(invUpdate);

        //  批量查询用户信息
        List<Long> userIds = enrollList.stream().map(EnrollDO::getUserId).distinct().toList();
        List<UserDO> userList = userMapper.selectByIds(userIds);

        Map<Long, String> userIdToUsername = userList.stream()
                .collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));

        // 3. 按班级分组 enroll
        Map<Long, List<String>> classIdToIdCards = enrollList.stream()
                .filter(enroll -> StrUtil.isNotBlank(userIdToUsername.get(enroll.getUserId())))
                .collect(Collectors.groupingBy(EnrollDO::getClassId, Collectors.mapping(enroll -> userIdToUsername
                        .get(enroll.getUserId()), Collectors.toList())));

        // 4. 批量更新 WorkerApplyDO
        for (Map.Entry<Long, List<String>> entry : classIdToIdCards.entrySet()) {
            Long classId = entry.getKey();
            List<String> idCards = entry.getValue().stream().distinct().toList();

            if (CollUtil.isNotEmpty(idCards)) {
                LambdaUpdateWrapper<WorkerApplyDO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(WorkerApplyDO::getClassId, classId)
                        .in(WorkerApplyDO::getIdCardNumber, idCards)
                        .set(WorkerApplyDO::getStatus, WorkerApplyReviewStatusEnum.ALTER_EXAM.getValue());
                workerApplyMapper.update(null, updateWrapper);
            }
        }

        // 4. 按班级分组 candidateId（用于更新 ted_org_class_candidate）
        Map<Long, List<Long>> classIdToCandidateIds = enrollList.stream()
                .collect(Collectors.groupingBy(EnrollDO::getClassId, Collectors.mapping(EnrollDO::getUserId, Collectors
                        .toList())));

        // 5. 批量更新 ted_org_class_candidate → 已考试
        for (Map.Entry<Long, List<Long>> entry : classIdToCandidateIds.entrySet()) {
            Long classId = entry.getKey();
            List<Long> candidateIds = entry.getValue().stream().distinct().toList();

            if (CollUtil.isNotEmpty(candidateIds)) {
                LambdaUpdateWrapper<OrgClassCandidateDO> updateWrapper = new LambdaUpdateWrapper<>();

                updateWrapper.eq(OrgClassCandidateDO::getClassId, classId)
                        .in(OrgClassCandidateDO::getCandidateId, candidateIds)
                        .set(OrgClassCandidateDO::getStatus, OrgClassCandidateStatusEnum.EXAMINED.getValue());

                orgClassCandidateMapper.update(null, updateWrapper);
            }

        }
        return Boolean.TRUE;
    }

    /**
     * 批量插入焊接实操成绩（适用于正常考生和缺考考生）
     */
    private void insertBatchWeldingOperScore(List<ExamRecordsDO> examRecordsList) {
        if (CollUtil.isEmpty(examRecordsList)) {
            return;
        }

        List<Long> recordIds = examRecordsList.stream().map(ExamRecordsDO::getId).toList();

        // 批量查询焊接项目
        List<Map<String, Object>> resultList = examRecordsMapper.selectWeldingProjectCodeByRecordIds(recordIds);

        // 转成 Map<recordId, weldingProjectCodes>
        Map<Long, String> recordWeldingMap = resultList.stream()
                .collect(Collectors.toMap(map -> ((Number) map.get("record_id")).longValue(), map -> (String) map
                        .get("welding_project_codes")));

        List<WeldingOperScoreDO> weldingScoreBatch = new ArrayList<>();
        for (ExamRecordsDO record : examRecordsList) {
            String weldingProjectCodes = recordWeldingMap.get(record.getId());
            if (ObjectUtil.isEmpty(weldingProjectCodes)) {
                continue;
            }

            for (String projectCode : weldingProjectCodes.split(",")) {
                WeldingOperScoreDO operScoreDO = new WeldingOperScoreDO();
                operScoreDO.setPlanId(record.getPlanId());
                operScoreDO.setRecordId(record.getId());
                operScoreDO.setCandidateId(record.getCandidateId());
                operScoreDO.setProjectCode(projectCode.trim());
                operScoreDO.setOperScore(0);
                weldingScoreBatch.add(operScoreDO);
            }
        }

        if (CollUtil.isNotEmpty(weldingScoreBatch)) {
            weldingOperScoreMapper.insertBatch(weldingScoreBatch);
        }
    }

    @Override
    public List<ProjectVo> getSelectOptions() {
        List<ProjectVo> projectVoList = examPlanMapper.getSelectOptions();
        for (ProjectVo projectVo : projectVoList) {
            projectVo.setDisabled(false);
        }
        return projectVoList;
    }

    /**
     * 批量导入考试计划
     *
     * @param file
     */
    @Transactional
    @Override
    public void importExcel(MultipartFile file) {
        // 1. 文件校验
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("仅支持Excel文件(.xlsx/.xls)");
        }

        // 定义时间解析器
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseStrict()
                .appendPattern("uuuu-MM-dd")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel中未找到有效工作表");
            }

            // 跳过前4行说明，第5行为空，第6行为表头
            int headerRowIndex = 3;
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new BusinessException("Excel表头不存在或模板结构错误");
            }

            // 校验表头
            String[] expectedHeaders = {"计划名称", "考试项目代码", "考试日期", "考试人数上限"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                String actual = (cell == null) ? "" : cell.getStringCellValue().trim();
                if (!expectedHeaders[i].equals(actual)) {
                    throw new BusinessException("模板错误：第 " + (i + 1) + " 列应为 [" + expectedHeaders[i] + "]，实际为 [" + actual + "]");
                }
            }

            // 2. 读取 Excel 数据到内存对象列表
            List<ExamPlanExcelRowDTO> rowList = new ArrayList<>();
            Map<String, Integer> planNameMap = new HashMap<>();
            for (int i = headerRowIndex + 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue; // 空行跳过

                int rowIndex = i + 1;
                String planName = getCellString(row.getCell(0));
                String projectCode = getCellString(row.getCell(1));
                String examStart = getCellString(row.getCell(2));
                String maxCandidates = getCellString(row.getCell(3));

                if (planName.isEmpty() && projectCode.isEmpty())
                    continue;

                // 基础非空校验
                if (planName.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：计划名称不能为空");
                if (projectCode.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：考试项目代码不能为空");
                if (examStart.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：考试日期不能为空");
                if (maxCandidates.isEmpty())
                    throw new BusinessException("第" + rowIndex + "行：人员限额不能为空");

                // Excel 内部计划名称重复
                if (planNameMap.containsKey(planName)) {
                    int firstRow = planNameMap.get(planName);
                    throw new BusinessException("第" + rowIndex + "行：计划名称在 Excel 内部重复，已在第" + firstRow + "行出现");
                } else {
                    planNameMap.put(planName, rowIndex);
                }

                // 检查项目代码是否存在
                ProjectDO projectDO = projectMapper.selectOne(new LambdaQueryWrapper<ProjectDO>()
                        .eq(ProjectDO::getProjectCode, projectCode));
                if (projectDO == null) {
                    throw new BusinessException("第" + rowIndex + "行：考试项目代码 [" + projectCode + "] 不存在");
                }

                // 禁止计划重名（数据库）
                ExamPlanDO existingPlan = baseMapper.selectOne(new QueryWrapper<ExamPlanDO>()
                        .eq("exam_plan_name", planName)
                        .eq("is_deleted", 0)
                        .eq("plan_type", projectDO.getProjectType()));
                if (existingPlan != null) {
                    throw new BusinessException("第" + rowIndex + "行：计划名称已存在系统中");
                }

                // 解析考试时间
                LocalDateTime examStartTime = parseDate(examStart, "考试日期", rowIndex, formatter)
                        .toLocalDate().atTime(9, 0);
                if (examStartTime.isBefore(LocalDateTime.now())) {
                    throw new BusinessException("第" + rowIndex + "行：考试开始时间不能早于当前时间");
                }
                // 报名开始时间：考试前 7 天 09:00
                LocalDateTime signupStartTime = examStartTime
                        .minusDays(7)
                        .toLocalDate()
                        .atTime(9, 0);


                // 报名结束时间：考试前 1 天 17:00
                LocalDateTime signupEndTime = examStartTime
                        .minusDays(1)
                        .toLocalDate()
                        .atTime(17, 0);

                rowList.add(new ExamPlanExcelRowDTO(planName, projectDO
                        .getId(), signupStartTime, signupEndTime, examStartTime, maxCandidates, projectDO
                        .getProjectType(), rowIndex));
            }

            // 批量插入
            List<ExamPlanDO> insertList = rowList.stream().map(row -> {
                ExamPlanDO plan = new ExamPlanDO();
                plan.setExamPlanName(row.getPlanName());
                plan.setExamProjectId(row.getProjectId());
                plan.setEnrollStartTime(row.getSignupStartTime());
                plan.setPlanType(row.getProjectType());
                plan.setEnrollEndTime(row.getSignupEndTime());
                plan.setStartTime(row.getExamStartTime());
                plan.setMaxCandidates(Integer.parseInt(row.getMaxCandidates()));
                plan.setPlanYear(String.valueOf(row.getExamStartTime().getYear()));
                return plan;
            }).collect(Collectors.toList());

            if (!insertList.isEmpty()) {
                // 批量插入计划
                baseMapper.insertBatch(insertList);
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("考试人数上限只能填写数字");
        } catch (IOException e) {
            throw new BusinessException("文件读取失败");
        } catch (EncryptedDocumentException e) {
            throw new BusinessException("文件加密无法读取");
        }
    }

    /**
     * 机构获取符合自身八大类的考试计划
     *
     * @param pageQuery
     * @param examPlanQuery
     * @return
     */
    @Override
    public PageResp<OrgExamPlanVO> orgGetPlanList(ExamPlanQuery examPlanQuery, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(examPlanQuery);
        queryWrapper.eq("tep.is_deleted", 0);
        // 执行分页查询
        IPage<OrgExamPlanVO> page = baseMapper.orgGetPlanList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper, userTokenDo.getUserId());

        // 将查询结果转换成 PageResp 对象
        PageResp<OrgExamPlanVO> pageResp = PageResp.build(page, OrgExamPlanVO.class);
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    /**
     * 解析日期
     *
     * @param text
     * @param fieldName
     * @param rowNum
     * @param formatter
     * @return
     */
    private LocalDateTime parseDate(String text, String fieldName, int rowNum, DateTimeFormatter formatter) {
        try {
            LocalDate date = LocalDate.parse(text.trim(), formatter);
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new BusinessException("第" + rowNum + "行：" + fieldName + " [ " + text + " ] 日期错误");
        }
    }

    /**
     * 安全读取单元格内容（统一转String）
     */
    private String getCellString(Cell cell) {
        if (cell == null)
            return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    /**
     * 重新update
     *
     * @param req
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExamPlanReq req, Long id) {
        // 1. 查询考试计划
        ExamPlanDO examPlanDO = baseMapper.selectById(id);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划");

        // 2. 校验最终确定考试时间及地点状态
        ValidationUtils.throwIf(PlanFinalConfirmedStatus.DIRECTOR_PENDING.getValue()
                .equals(req.getIsFinalConfirmed()), "最终确定考试时间及地点已确认");

        // 3. 解析报名时间段
        List<String> enrollList = req.getEnrollList();
        if (CollUtil.isEmpty(enrollList) || enrollList.size() < 2) {
            ValidationUtils.validate("报名时间列表不完整");
        }
        req.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        req.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));

        // 校验报名结束时间不能晚于考试开始时间
        //        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(req.getEnrollEndTime(), req
        //                .getStartTime()), "报名结束时间不能晚于考试开始时间");

        // 考试开始时间不能早于当前时间
        //        ValidationUtils.throwIf(req.getStartTime().isBefore(LocalDateTime.now().minusSeconds(2)), "考试开始时间不能早于当前时间");
        Integer invigilatorCount = req.getInvigilatorCount();
        List<Long> classroomIds = new ArrayList<>();
        List<Long> theoryIds = req.getTheoryClassroomId();
        List<Long> operIds = req.getOperationClassroomId();
        ValidationUtils.throwIf(invigilatorCount < ((theoryIds == null ? 0 : theoryIds.size()) + (operIds == null
                ? 0
                : operIds.size())), "所设置的监考员人数不足以分配到全部考场");
        // 判断有没有启用监考员劳务费
        LaborFeeDO laborFeeDO = laborFeeMapper.selectOne(new LambdaQueryWrapper<LaborFeeDO>()
                .eq(LaborFeeDO::getIsEnabled, Boolean.TRUE));
        ValidationUtils.throwIfNull(laborFeeDO, "当前未配置启用中的监考劳务费规则");

        if (!CollectionUtils.isEmpty(theoryIds)) {
            classroomIds.addAll(theoryIds);
        }
        if (!CollectionUtils.isEmpty(operIds)) {
            classroomIds.addAll(operIds);
        }

        // 2. 查询报名记录
        //        ValidationUtils.throwIfEmpty(enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
        //                .eq(EnrollDO::getExamPlanId, id)), "未查询到考生报名信息");

        //        if (!CollectionUtils.isEmpty(classroomIds)) {
        //            List<String> conflictClassrooms = baseMapper.listConflictClassrooms(req.getStartTime(), classroomIds);
        //            ValidationUtils.throwIfNotEmpty(conflictClassrooms, "以下考场当天已存在考试：" + String.join("、", conflictClassrooms));
        //        }

        //        // 4.如果是新的考场人数小于之前的考场人数，无法成功
        //        LambdaQueryWrapper<ExamineePaymentAuditDO> examineePaymentAuditDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //        examineePaymentAuditDOLambdaQueryWrapper.eq(ExamineePaymentAuditDO::getExamPlanId, id)
        //                .eq(ExamineePaymentAuditDO::getAuditStatus, PaymentAuditStatusEnum.APPROVED.getValue());
        //        Long candidateCount = examineePaymentAuditMapper.selectCount(examineePaymentAuditDOLambdaQueryWrapper);
        //        Long totalCapacity = classroomMapper.selectByIds(req.getClassroomId())
        //                .stream()
        //                .mapToLong(ClassroomDO::getMaxCandidates)
        //                .sum();
        //        ValidationUtils.throwIf(candidateCount > totalCapacity, String
        //                .format("已报名 %d 人，但所选考场最多只能容纳 %d 人，请重新选择考场", candidateCount, totalCapacity));

        // 5. 如果是检验类型，校验报名人员是否全部审核通过
        if (ExamPlanTypeEnum.INSPECTION.getValue().equals(examPlanDO.getPlanType())) {
            long pendingCount = specialCertificationApplicantMapper
                    .selectCount(new LambdaQueryWrapper<SpecialCertificationApplicantDO>()
                            .eq(SpecialCertificationApplicantDO::getPlanId, id)
                            .notIn(SpecialCertificationApplicantDO::getStatus, Arrays
                                    .asList(SpecialCertificationApplicantAuditStatusEnum.APPROVED
                                            .getValue(), SpecialCertificationApplicantAuditStatusEnum.FAKE_MATERIAL.getValue())));
            ValidationUtils.throwIf(pendingCount > 0, "存在未审核考试报名申请，请审核后再确认考试时间或地点");
        }

        // 6. 校验考试人员缴费状态
        long unpaidCount = examineePaymentAuditMapper.selectCount(new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                .eq(ExamineePaymentAuditDO::getExamPlanId, id)
                .notIn(ExamineePaymentAuditDO::getAuditStatus, Arrays.asList(PaymentAuditStatusEnum.APPROVED
                        .getValue(), PaymentAuditStatusEnum.REFUNDED.getValue())));
        ValidationUtils.throwIf(unpaidCount > 0, "存在考试人员未提交缴费通知单或审核未通过，请到考生缴费审核管理完成审核");

        // 7. 计算考试结束时间
        //        ProjectDO projectDO = projectMapper.selectById(req.getExamProjectId());
        //        ValidationUtils.throwIfNull(projectDO, "未查询到考试项目");

        //        req.setStartTime(req.getStartTime());
        //        req.setEndTime(req.getStartTime().plusMinutes(projectDO.getExamDuration()));

        req.setIsFinalConfirmed(PlanFinalConfirmedStatus.DIRECTOR_PENDING.getValue());

        // 8.在选择的考试的时间，考场有没有是否有别的考试
        //        ValidationUtils.throwIf(hasClassroomTimeConflict(req.getStartTime(), req.getEndTime(), req
        //                .getClassroomId(), id) > 0, "所选考场在所选时间段前后30分钟内已有考试计划，请调整考试时间或更换考场。");

        req.setAssignType(InvigilatorAssignTypeEnum.RANDOM_FIRST.getValue());

        // 判断题库是否够考试
        questionBankService.generateExamQuestionBank(id);

        // 10.插入考场和计划关系
        if (!CollectionUtils.isEmpty(classroomIds)) {
            // 添加考场信息
            examPlanMapper.savePlanClassroom(examPlanDO.getId(), classroomIds);
        }

        List<Long> alterHandClassroomIds = new ArrayList<>();
        int alterInvigilatorCount = 0;
        // 处理理论班级
        if (!CollectionUtils.isEmpty(theoryIds)) {
            List<ClassroomInvigilatorDTO> hasExamStartDateList = baseMapper
                    .findInvigilatorsByDateAndClassrooms(examPlanDO.getStartTime(), theoryIds);
            if (!CollectionUtils.isEmpty(hasExamStartDateList)) {
                // 如果只有理论考试，那么把所有的监考员都安排到理论考场
                if (CollectionUtils.isEmpty(operIds)) {
                    List<PlanInvigilateDO> invigilateDOS = hasExamStartDateList.stream().map(item -> {
                        PlanInvigilateDO record = new PlanInvigilateDO();
                        record.setExamPlanId(examPlanDO.getId());
                        record.setInvigilatorId(item.getInvigilatorId());
                        record.setClassroomId(item.getClassroomId());
                        record.setInvigilateStatus(InvigilateStatusEnum.NOT_START.getValue());
                        record.setTheoryFee(laborFeeDO.getTheoryFee());
                        record.setPracticalFee(laborFeeDO.getPracticalFee());
                        return record;
                    }).collect(Collectors.toList());

                    if (invigilateDOS.size() > invigilatorCount) {
                        Collections.shuffle(invigilateDOS);
                        invigilateDOS = invigilateDOS.subList(0, invigilatorCount);
                    }
                    // 批量插入
                    planInvigilateMapper.insertBatch(invigilateDOS);
                    // 记录已处理的考场 & 数量
                    alterHandClassroomIds.addAll(theoryIds);
                    alterInvigilatorCount += invigilateDOS.size();
                } else {
                    // 先保证理论考试必须有一个监考员
                    ClassroomInvigilatorDTO classroomInvigilatorDTO = hasExamStartDateList.get(0);
                    PlanInvigilateDO record = new PlanInvigilateDO();
                    // 考试计划 ID
                    record.setExamPlanId(examPlanDO.getId());
                    // 监考员 ID
                    record.setInvigilatorId(classroomInvigilatorDTO.getInvigilatorId());
                    // 考场号
                    Long classroomId = classroomInvigilatorDTO.getClassroomId();
                    record.setClassroomId(classroomId);
                    // 监考状态
                    record.setInvigilateStatus(InvigilateStatusEnum.NOT_START.getValue());
                    record.setTheoryFee(laborFeeDO.getTheoryFee());
                    record.setPracticalFee(laborFeeDO.getPracticalFee());
                    planInvigilateMapper.insert(record);
                    alterHandClassroomIds.add(classroomId);
                    alterInvigilatorCount++;
                }
            } else {
                // 如果当天没考试，随机分配理论考场一个监考老师
                // 11.随机分配监考人员
                randomInvigilator(theoryIds, examPlanDO.getStartTime(), id, theoryIds
                        .size(), laborFeeDO, ExamTypeEnum.THEORY.getValue());
                alterHandClassroomIds.addAll(theoryIds);
                alterInvigilatorCount += theoryIds.size();
            }
        }

        // 处理实操考场
        if (!CollectionUtils.isEmpty(operIds)) {
            List<ClassroomInvigilatorDTO> hasOperExamStartDateList = baseMapper
                    .findInvigilatorsByDateAndClassrooms(examPlanDO.getStartTime(), operIds);
            // 今天已有考试的考场复用
            if (!CollectionUtils.isEmpty(hasOperExamStartDateList)) {
                // 按考场分组
                Map<Long, List<ClassroomInvigilatorDTO>> classroomMap = hasOperExamStartDateList.stream()
                        .collect(Collectors.groupingBy(ClassroomInvigilatorDTO::getClassroomId));

                List<PlanInvigilateDO> invigilateDOS = new ArrayList<>();

                // 每个考场只取 1 个监考员
                for (Map.Entry<Long, List<ClassroomInvigilatorDTO>> entry : classroomMap.entrySet()) {
                    ClassroomInvigilatorDTO item = entry.getValue().get(0);
                    PlanInvigilateDO record = new PlanInvigilateDO();
                    record.setExamPlanId(examPlanDO.getId());
                    record.setInvigilatorId(item.getInvigilatorId());
                    record.setClassroomId(item.getClassroomId());
                    record.setInvigilateStatus(InvigilateStatusEnum.NOT_START.getValue());
                    record.setTheoryFee(laborFeeDO.getTheoryFee());
                    record.setPracticalFee(laborFeeDO.getPracticalFee());
                    invigilateDOS.add(record);
                    alterHandClassroomIds.add(item.getClassroomId());
                }
                // 批量插入
                planInvigilateMapper.insertBatch(invigilateDOS);

                alterInvigilatorCount += invigilateDOS.size();
            } else {
                // 今天没有考试，随机分配
                randomInvigilator(operIds, examPlanDO.getStartTime(), id, operIds
                        .size(), laborFeeDO, ExamTypeEnum.PRACTICE.getValue());
                alterHandClassroomIds.addAll(operIds);
                alterInvigilatorCount += operIds.size();
            }
        }

        // 判断是否所有的考场都安排了监考员
        if (alterHandClassroomIds.size() < classroomIds.size()) {
            // 找出没有安排监考员的考场
            Set<Long> unAssignedClassroomSet = new HashSet<>(classroomIds);
            unAssignedClassroomSet.removeAll(alterHandClassroomIds);
            List<Long> unAssignedClassroomIds = new ArrayList<>(unAssignedClassroomSet);
            // 随机安排
            randomInvigilator(unAssignedClassroomIds, examPlanDO.getStartTime(), id, unAssignedClassroomIds
                    .size(), laborFeeDO, ExamTypeEnum.PRACTICE.getValue());
            alterHandClassroomIds.addAll(unAssignedClassroomIds);
            alterInvigilatorCount += unAssignedClassroomIds.size();
        }
        // 如果考场都安排了监考员，但已处理的考场数量小于前端传来的要求整个计划所需监考员，需要给某些考场进行再添加监考员，指导满足前端需要的监考员数量
        if (alterInvigilatorCount < invigilatorCount) {
            // 1. 还差多少个监考员
            int remainInvigilatorCount = invigilatorCount - alterInvigilatorCount;
            List<Long> needAddInvigilatorClassroomIds;
            // 2. 如果可选考场数 <= 需要补的数量，全部返回
            if (alterHandClassroomIds.size() <= remainInvigilatorCount) {
                needAddInvigilatorClassroomIds = new ArrayList<>(alterHandClassroomIds);
            } else {
                // 3. 否则随机取 remainInvigilatorCount 个
                List<Long> shuffledClassroomIds = new ArrayList<>(alterHandClassroomIds);
                Collections.shuffle(shuffledClassroomIds);
                needAddInvigilatorClassroomIds = shuffledClassroomIds.stream()
                        .limit(remainInvigilatorCount)
                        .collect(Collectors.toList());
            }
            // 随机安排
            randomInvigilator(needAddInvigilatorClassroomIds, examPlanDO
                    .getStartTime(), id, remainInvigilatorCount, laborFeeDO, ExamTypeEnum.PRACTICE.getValue());
        }

        // 9. 执行更新
        super.update(req, id);
    }

    private void randomInvigilator(List<Long> classroomIds,
                                   LocalDateTime startTime,
                                   Long planId,
                                   Integer invigilatorNum,
                                   LaborFeeDO laborFeeDO,
                                   Integer examType) {
        // 清除缓存，防止没查数据库
        SqlSession sqlSession = SqlHelper.sqlSession(getEntityClass());
        sqlSession.clearCache();
        // 先找出当前计划已存在的监考员，同一个计划不能有相同的监考员
        List<PlanInvigilateDO> planInvigilateDOS = planInvigilateMapper
                .selectList(new LambdaQueryWrapper<PlanInvigilateDO>().eq(PlanInvigilateDO::getExamPlanId, planId)
                        .eq(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.NOT_START.getValue())
                        .select(PlanInvigilateDO::getInvigilatorId));
        List<Long> planAlterExistInvigilateIds = planInvigilateDOS.stream()
                .map(PlanInvigilateDO::getInvigilatorId)
                .toList();
        // 获取所选时间段有空闲的监考人员并上传了对应八大类资质的
        List<AvailableInvigilatorResp> availableInvigilatorList = planInvigilateMapper
                .selectAvailableInvigilatorsExcludingAssigned(startTime, planId, invigilatorId, planAlterExistInvigilateIds, examType, specialEquipmentManageTypeId);
        ValidationUtils.throwIf(ObjectUtil.isEmpty(availableInvigilatorList) || availableInvigilatorList
                .size() < invigilatorNum, "当前时间段可分配的监考员或有资质的监考员数量不足");
        Collections.shuffle(availableInvigilatorList);
        List<AvailableInvigilatorResp> assignedAvailableInvigilatorList = availableInvigilatorList
                .subList(0, invigilatorNum);
        // 还要随机分配给classRoomNum
        Map<Long, List<AvailableInvigilatorResp>> assignmentMap = new HashMap<>();

        // 初始化 map
        classroomIds.forEach(cid -> assignmentMap.put(cid, new ArrayList<>()));

        // 第一轮：确保每个考场至少分配一个监考员
        for (int i = 0; i < classroomIds.size(); i++) {
            assignmentMap.get(classroomIds.get(i)).add(assignedAvailableInvigilatorList.get(i));
        }

        // 剩余监考员继续随机分配
        for (int i = classroomIds.size(); i < assignedAvailableInvigilatorList.size(); i++) {
            Long randomClassroomId = classroomIds.get(new Random().nextInt(classroomIds.size()));
            assignmentMap.get(randomClassroomId).add(assignedAvailableInvigilatorList.get(i));
        }

        List<PlanInvigilateDO> records = new ArrayList<>();

        for (Map.Entry<Long, List<AvailableInvigilatorResp>> entry : assignmentMap.entrySet()) {
            Long classroomId = entry.getKey();
            List<AvailableInvigilatorResp> list = entry.getValue();

            for (AvailableInvigilatorResp invigilatorResp : list) {
                PlanInvigilateDO record = new PlanInvigilateDO();
                // 考试计划 ID
                record.setExamPlanId(planId);
                // 监考员 ID
                record.setInvigilatorId(invigilatorResp.getId());
                // 考场号
                record.setClassroomId(classroomId);
                // 监考状态
                record.setInvigilateStatus(InvigilateStatusEnum.NOT_START.getValue());
                record.setTheoryFee(laborFeeDO.getTheoryFee());
                record.setPracticalFee(laborFeeDO.getPracticalFee());
                records.add(record);
            }
        }
        planInvigilateMapper.insertBatch(records);
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        QueryWrapper<PlanInvigilateDO> wrapper = new QueryWrapper<PlanInvigilateDO>();
        wrapper.in("exam_plan_id", ids);
        planInvigilateMapper.delete(wrapper);
        //删除监考计划关联表
        super.delete(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdatePlanMaxCandidates(List<ExamPlanDTO> planList) {
        if (planList != null && !planList.isEmpty()) {
            examPlanMapper.batchUpdatePlanMaxCandidates(planList);
        }
    }

    /**
     * 重新随机分配考试计划的监考员
     *
     * @param planId
     * @param invigilatorNum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reRandomInvigilators(Long planId, Integer invigilatorNum) {
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划");
        // 判断是否已有监考员确认，如果有无法再次随机
        //        Long notStartCount = planInvigilateMapper.selectCount(new LambdaQueryWrapper<PlanInvigilateDO>()
        //            .eq(PlanInvigilateDO::getExamPlanId, examPlanDO.getId())
        //            .eq(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.NOT_START.getValue()));
        //        ValidationUtils.throwIf(notStartCount > 0, "已有监考员完成确认，当前无法重新随机分配！");

        // 判断有没有启用监考员劳务费
        LaborFeeDO laborFeeDO = laborFeeMapper.selectOne(new LambdaQueryWrapper<LaborFeeDO>()
                .eq(LaborFeeDO::getIsEnabled, Boolean.TRUE));
        ValidationUtils.throwIfNull(laborFeeDO, "当前未配置启用中的监考劳务费规则");

        // 1. 先删除原监考员
        planInvigilateMapper.delete(new LambdaQueryWrapper<PlanInvigilateDO>()
                .eq(PlanInvigilateDO::getExamPlanId, planId));

        // 2. 查询所有考场 classroomId
        List<Long> classroomIds = planClassroomMapper.selectList(new LambdaQueryWrapper<PlancalssroomDO>()
                .eq(PlancalssroomDO::getPlanId, planId)
                .select(PlancalssroomDO::getClassroomId)).stream().map(PlancalssroomDO::getClassroomId).toList();

        // 3. 随机分配监考逻辑
        randomInvigilator(classroomIds, examPlanDO
                .getStartTime(), planId, invigilatorNum, laborFeeDO, ExamTypeEnum.PRACTICE.getValue());
        // 4.修改计划分配监考员类型
        baseMapper.update(new LambdaUpdateWrapper<ExamPlanDO>()
                .set(ExamPlanDO::getAssignType, InvigilatorAssignTypeEnum.RANDOM_SECOND.getValue())
                .eq(ExamPlanDO::getId, planId));
        return true;
    }

    /**
     * 获取可用监考员
     *
     * @param planId
     * @return
     */
    @Override
    public List<AvailableInvigilatorResp> getAvailableInvigilator(Long planId, Long rejectedInvigilatorId) {
        // 1. 查询考试计划
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到计划信息");

        // 2. 查询空闲监考员
        List<AvailableInvigilatorResp> availableInvigilatorResps = planInvigilateMapper
                .selectAvailableInvigilators(examPlanDO.getStartTime(), planId, invigilatorId);

        // 3. 查询已经安排的监考员
        List<Long> assignedInvigilatorIds = planInvigilateMapper.selectList(new LambdaQueryWrapper<PlanInvigilateDO>()
                .eq(PlanInvigilateDO::getExamPlanId, planId)
                .eq(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.NOT_START.getValue())
                .select(PlanInvigilateDO::getInvigilatorId)).stream().map(PlanInvigilateDO::getInvigilatorId).toList();

        // 4. 过滤已经安排的监考员
        if (ObjectUtil.isNotEmpty(assignedInvigilatorIds)) {
            availableInvigilatorResps = availableInvigilatorResps.stream()
                    .filter(resp -> !assignedInvigilatorIds.contains(resp.getId()))
                    .toList();
        }

        // 5. 返回最终可用监考员列表
        return availableInvigilatorResps;
    }

    /**
     * 中心主任确认考试
     *
     * @param planId
     * @param isFinalConfirmed
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean centerDirectorConform(Long planId, Integer isFinalConfirmed) {
        // 查询计划
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划信息");

        // 查询理论考场
        List<Long> classroomIds = examPlanMapper.getPlanExamTheoryClassroom(planId);
        ValidationUtils.throwIfEmpty(classroomIds, "考试计划未分配理论考场");

        // 查询报名记录
        boolean isConfirmed = PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue().equals(isFinalConfirmed);
        List<EnrollDO> enrollList = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
                .eq(EnrollDO::getExamPlanId, planId)
                .orderByAsc(EnrollDO::getId));
        ValidationUtils.throwIf(ObjectUtil.isEmpty(enrollList) && isConfirmed, "未查询到考生报名信息");

        // 增加考试计划和班级关联表
        planApplyClassMapper.insertBatch(enrollList.stream().map(EnrollDO::getClassId).distinct().map(classId -> {
            PlanApplyClassDO planApplyClassDO = new PlanApplyClassDO();
            planApplyClassDO.setClassId(classId);
            planApplyClassDO.setPlanId(planId);
            return planApplyClassDO;
        }).toList());

        // 查询监考员列表
        List<InvigilatorAssignResp> invigilatorList = planInvigilateMapper.getListByPlanId(planId);
        ValidationUtils.throwIfEmpty(invigilatorList, "未选择监考员");

        // 查询项目信息
        ProjectDO projectDO = projectMapper.selectById(examPlanDO.getExamProjectId());

        // 更新计划状态
        LambdaUpdateWrapper<ExamPlanDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ExamPlanDO::getId, planId).set(ExamPlanDO::getIsFinalConfirmed, isFinalConfirmed);

        // 主任驳回
        if (PlanFinalConfirmedStatus.DIRECTOR_REJECTED.getValue().equals(isFinalConfirmed)) {
            updateWrapper.set(ExamPlanDO::getAssignType, null);
            planInvigilateMapper.delete(new LambdaQueryWrapper<PlanInvigilateDO>()
                    .eq(PlanInvigilateDO::getExamPlanId, planId));
            planClassroomMapper.delete(new LambdaQueryWrapper<PlancalssroomDO>()
                    .eq(PlancalssroomDO::getPlanId, planId));
        }

        boolean success = baseMapper.update(updateWrapper) > 0;

        // 生成开考密码（每个考场统一）
        Map<Long, List<InvigilatorAssignResp>> groupByClassroom = invigilatorList.stream()
                .collect(Collectors.groupingBy(InvigilatorAssignResp::getClassroomId));
        List<PlanInvigilateDO> updateList = new ArrayList<>();
        for (Map.Entry<Long, List<InvigilatorAssignResp>> entry : groupByClassroom.entrySet()) {
            String examPassword = generateExamPassword();
            for (InvigilatorAssignResp item : entry.getValue()) {
                PlanInvigilateDO planInvigilateDO = new PlanInvigilateDO();
                planInvigilateDO.setId(item.getId());
                planInvigilateDO.setExamPassword(examPassword);
                updateList.add(planInvigilateDO);
            }
        }
        if (!updateList.isEmpty()) {
            planInvigilateMapper.updateBatchById(updateList);
        }

        // 主任确认 && 作业人员考试
        if (success && isConfirmed && ExamPlanTypeEnum.WORKER.getValue().equals(examPlanDO.getPlanType())) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
            Collections.shuffle(classroomIds, random);

            // 查询用户信息
            List<Long> userIds = enrollList.stream().map(EnrollDO::getUserId).distinct().toList();
            Map<Long, UserDO> userMap = userMapper.selectBatchIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(UserDO::getId, u -> u));

            // 查询班级信息
            List<Long> classIds = enrollList.stream().map(EnrollDO::getClassId).distinct().toList();
            Map<Long, String> classNameMap = orgClassMapper.selectByIds(classIds)
                    .stream()
                    .collect(Collectors.toMap(OrgClassDO::getId, c -> Optional.ofNullable(c.getClassName()).orElse("")));

            // ======= 1. 先批量生成报名表里的准考证号 =======
            List<EnrollDO> enrollUpdateList = new ArrayList<>();
            String examDate = examPlanDO.getStartTime().format(formatter);
            String projectCode = projectDO.getProjectCode();
            String redisKey = RedisConstant.EXAM_NUMBER_KEY + projectCode + ":" + examDate + ":" + planId;
            redisTemplate.delete(redisKey);

            for (EnrollDO enroll : enrollList) {
                Long classroomId = classroomIds.get(random.nextInt(classroomIds.size()));
                String examNumber = projectCode + "B" + examDate + String.format("%04d", enroll.getSeatId());
                enroll.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                enroll.setClassroomId(classroomId);
                enrollUpdateList.add(enroll);
            }

            // 批量更新报名表
            if (!enrollUpdateList.isEmpty()) {
                enrollMapper.updateBatchById(enrollUpdateList);
            }

            // ======= 2. 再生成准考证和试卷 =======
            List<WorkerExamTicketDO> ticketSaveList = new ArrayList<>();
            List<CandidateExamPaperDO> candidateExamPaperDOList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            for (EnrollDO enroll : enrollUpdateList) {
                UserDO user = userMap.get(enroll.getUserId());
                if (user == null)
                    continue;

                // 生成准考证 PDF
                String ticketUrl = safeGenerateWorkerTicket(user.getId(), user.getUsername(), user.getNickname(), enroll
                        .getExamNumber(), enroll.getClassId(), classNameMap.get(enroll.getClassId()));

                WorkerExamTicketDO ticket = new WorkerExamTicketDO();
                ticket.setEnrollId(enroll.getId());
                ticket.setCandidateName(user.getNickname());
                ticket.setTicketUrl(ticketUrl);
                ticketSaveList.add(ticket);

                // 生成试卷
                ExamPaperVO examPaperVO = questionBankService.generateExamQuestionBank(planId);
                CandidateExamPaperDO paper = new CandidateExamPaperDO();
                try {
                    paper.setPaperJson(objectMapper.writeValueAsString(examPaperVO));
                } catch (Exception e) {
                    throw new BusinessException("生成试卷失败");
                }
                paper.setEnrollId(enroll.getId());
                candidateExamPaperDOList.add(paper);
            }

            // 批量插入准考证
            if (!ticketSaveList.isEmpty()) {
                workerExamTicketMapper.insertBatch(ticketSaveList);
            }

            // 批量插入试卷
            if (!candidateExamPaperDOList.isEmpty()) {
                candidateExamPaperMapper.insertBatch(candidateExamPaperDOList);
            }
        }
        return success;
    }

    /**
     * 调整考试/报名时间
     *
     * @param req
     * @param planId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean adjustPlanTime(AdjustPlanTimeReq req, Long planId) {
        // 1. 查询考试计划
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "未查询到考试计划");

        // 2. 解析报名时间段
        List<String> enrollList = req.getEnrollList();
        if (CollUtil.isEmpty(enrollList) || enrollList.size() < 2) {
            ValidationUtils.validate("报名时间列表不完整");
        }
        ExamPlanDO update = new ExamPlanDO();
        update.setEnrollStartTime(DateUtil.parse(enrollList.get(0)));
        update.setEnrollEndTime(DateUtil.parse(enrollList.get(1)));

        // 校验报名结束时间不能晚于考试开始时间
        //        ValidationUtils.throwIf(!DateUtil.validateEnrollmentTime(update.getEnrollEndTime(), req
        //                .getStartTime()), "报名结束时间不能晚于考试开始时间");

        // 考试开始时间不能早于当前时间
        //        ValidationUtils.throwIf(req.getStartTime().isBefore(LocalDateTime.now().minusSeconds(2)), "考试开始时间不能早于当前时间");

        update.setId(planId);
        update.setStartTime(req.getStartTime());
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 监考员获取监考计划列表
     *
     * @param examPlanQuery 考试计划查询参数
     * @param pageQuery     分页参数
     * @return 分页结果
     */
    @Override
    public PageResp<InvigilateExamPlanVO> invigilateGetPlanList(ExamPlanQuery examPlanQuery, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(examPlanQuery);
        queryWrapper.eq("tep.is_deleted", 0);
        Integer invigilateStatus = examPlanQuery.getInvigilateStatus();
        if (invigilateStatus != null) {
            if (InvigilateStatusEnum.FINISHED.getValue().equals(invigilateStatus)) {
                queryWrapper.eq("tpi.invigilate_status", InvigilateStatusEnum.FINISHED.getValue());
            } else {
                queryWrapper.in("tpi.invigilate_status", InvigilateStatusEnum.NOT_START
                        .getValue(), InvigilateStatusEnum.DURING_NVIGILATION.getValue());
            }
        }

        // 执行分页查询
        IPage<InvigilateExamPlanVO> page = baseMapper.invigilateGetPlanList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper, userTokenDo.getUserId());

        // 将查询结果转换成 PageResp 对象
        PageResp<InvigilateExamPlanVO> pageResp = PageResp.build(page, InvigilateExamPlanVO.class);
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    /**
     * 监考员进行开考
     *
     * @param req
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamCandidateInfoVO startExam(ExamPlanStartReq req) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        // 根据计划id、监考考场获取监考信息
        Long planId = req.getExamPlanId();
        Long classroomId = req.getClassroomId();
        // 查询计划信息
        ExamPlanDO examPlanDO = baseMapper.selectById(planId);
        ValidationUtils.throwIfNull(examPlanDO, "考试计划信息不存在");
        ValidationUtils.throwIf(!PlanFinalConfirmedStatus.DIRECTOR_CONFIRMED.getValue()
                .equals(examPlanDO.getIsFinalConfirmed()), "考试计划尚未完成最终确认");
        ValidationUtils.throwIf(PlanConstant.OVER.getStatus().equals(examPlanDO.getStatus()), "考试已结束");

        PlanInvigilateDO planInvigilateDO = planInvigilateMapper.selectOne(new LambdaQueryWrapper<PlanInvigilateDO>()
                .eq(PlanInvigilateDO::getExamPlanId, planId)
                .eq(PlanInvigilateDO::getClassroomId, classroomId)
                .eq(PlanInvigilateDO::getInvigilatorId, userTokenDo.getUserId())
                .last("limit 1")
                .select(PlanInvigilateDO::getExamPassword));
        ValidationUtils.throwIfNull(planInvigilateDO, "当前考场暂无可开考的监考任务");

        boolean statusIsSuccess = PlanConstant.SUCCESS.getStatus().equals(examPlanDO.getStatus());

        ValidationUtils.throwIf(statusIsSuccess && !req.getExamPassword()
                .equals(planInvigilateDO.getExamPassword()), "开考密码错误，请核对后重试");
        LocalDateTime startTime = examPlanDO.getStartTime();
        if (statusIsSuccess) {
            LocalDateTime now = LocalDateTime.now();
            // 开始时间 - 当前时间 > 15分钟： 考前45分钟才可以进去
            ValidationUtils.throwIf(Duration.between(now, startTime).toMinutes() > 15, "请于考前15分钟内进入");
            // 修改考试计划状态
            baseMapper.update(new LambdaUpdateWrapper<ExamPlanDO>().set(ExamPlanDO::getStatus, PlanConstant.EXAM_BEGUN
                    .getStatus()).eq(ExamPlanDO::getId, planId));

            // 修改监考员状态
            planInvigilateMapper.update(new LambdaUpdateWrapper<PlanInvigilateDO>()
                    .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.DURING_NVIGILATION.getValue())
                    .eq(PlanInvigilateDO::getClassroomId, classroomId)
                    .eq(PlanInvigilateDO::getExamPlanId, planId));
        }

        // 查出考场对应的信息
        ClassroomDO classroomDO = classroomMapper.selectById(classroomId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        String formattedStartTime = startTime.format(formatter);
        ExamCandidateInfoVO examCandidateInfoVO = new ExamCandidateInfoVO();
        examCandidateInfoVO.setPlanId(planId);
        examCandidateInfoVO.setPlanName(examPlanDO.getExamPlanName());
        examCandidateInfoVO.setExamTime(formattedStartTime);
        examCandidateInfoVO.setClassroomId(classroomId);
        examCandidateInfoVO.setClassroomName(classroomDO.getClassroomName());

        return examCandidateInfoVO;
    }

    /**
     * 根据考生身份证获取考生的所有考试准考证号
     *
     * @param username
     * @return
     */
    @Override
    public List<CascaderOptionResp> getExamNumbersByUsername(String username) {
        // 解密用户名
        String verifyUsername = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(username));
        ValidationUtils.throwIfNull(verifyUsername, "请核对身份证号是否正确");

        String usernameDB = aesWithHMAC.encryptAndSign(verifyUsername);

        UserDO user = userMapper.selectByUsername(usernameDB);
        ValidationUtils.throwIf(cn.hutool.core.util.ObjectUtil.isEmpty(user), "请核对身份证号是否正确");

        List<Map<String, Object>> list = baseMapper.findExamPlansByUsername(usernameDB);
        ValidationUtils.throwIfEmpty(list, "未查询到考试");

        // 过滤掉 exam_plan_id 为 null 的记录再分组
        Map<Long, List<Map<String, Object>>> groupMap = list.stream()
                .filter(m -> m.get("exam_plan_id") != null && m.get("exam_number") != null)
                .collect(Collectors.groupingBy(m -> (Long) m.get("exam_plan_id")));

        List<CascaderOptionResp> cascaderList = new ArrayList<>();

        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupMap.entrySet()) {
            Long planId = entry.getKey();
            String planName = (String) entry.getValue().get(0).get("exam_plan_name");

            List<CascaderOptionResp> children = entry.getValue().stream().map(m -> {
                String examNumber = (String) m.get("exam_number");
                String decryptedNumber = aesWithHMAC.verifyAndDecrypt(examNumber);
                return new CascaderOptionResp(decryptedNumber, decryptedNumber);
            }).toList();

            cascaderList.add(new CascaderOptionResp(planId, planName, children));
        }

        return cascaderList;
    }

    /**
     * 根据计划考试人员类型获取项目-考试计划级联选择器
     *
     * @param planType
     * @return
     */
    @Override
    public List<CascaderPlanResp> getCascaderProjectPlan(Integer planType, Boolean isOrgQuery) {
        // 1. 查询数据库，获取项目和计划信息
        List<Map<String, Object>> projectPlanList = baseMapper.selectListByPlanType(planType);

        if (projectPlanList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 按项目分组
        Map<Long, List<Map<String, Object>>> projectGroupMap = projectPlanList.stream()
                .collect(Collectors.groupingBy(item -> (Long) item.get("project_id")));

        // 3. 构建 CascaderPlanResp 结构
        List<CascaderPlanResp> result = projectGroupMap.entrySet().stream().map(entry -> {
            Long projectId = entry.getKey();
            List<Map<String, Object>> plans = entry.getValue();

            // 项目名称为空则跳过这个项目
            String projectName = (String) plans.get(0).get("project_name");
            if (projectName == null)
                return null;

            CascaderPlanResp projectResp = new CascaderPlanResp();
            projectResp.setValue(projectId);
            projectResp.setLabel(projectName);

            // 子级计划，过滤掉 plan_name 为 null 的记录
            List<CascaderPlanResp> children = plans.stream().filter(p -> p.get("exam_plan_name") != null).map(p -> {
                CascaderPlanResp planResp = new CascaderPlanResp();
                planResp.setValue((Long) p.get("plan_id"));
                planResp.setLabel((String) p.get("exam_plan_name"));
                // 不设置 children，保持两层结构
                return planResp;
            }).toList();

            projectResp.setChildren(children);
            return projectResp;
        }).filter(Objects::nonNull).toList();

        if (Boolean.TRUE.equals(isOrgQuery)) {
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            List<Long> orgBindProjectIds = baseMapper.selectOrgBindProjectId(userTokenDo.getUserId());

            if (orgBindProjectIds == null || orgBindProjectIds.isEmpty()) {
                return Collections.emptyList();
            }

            result = result.stream().filter(p -> orgBindProjectIds.contains(p.getValue())).toList();
        }

        return result;
    }

    /**
     * 根据班级列表获取每个班级在考试计划下的报名人数、考试人数、及格人数、成绩录入情况和证书生成情况
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamPlanResp> getClassExamStatsPage(ExamPlanQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        queryWrapper.eq("tep.status", ExamPlanStatusEnum.STARTED.getValue());
        queryWrapper.eq("tep.plan_type", ExamPlanTypeEnum.WORKER.getValue());
        super.sort(queryWrapper, pageQuery);

        IPage<ExamPlanDetailResp> page = baseMapper.selectExamPlanPagegetClassExamStatsPage(new Page<>(pageQuery
                .getPage(), pageQuery.getSize()), queryWrapper, roadExamTypeId);

        PageResp<ExamPlanResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 机构根据班级列表获取每个班级在考试计划下的报名人数、考试人数、及格人数、成绩录入情况
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamPlanResp> getClassExamStatsPageForOrg(ExamPlanQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamPlanDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tep.is_deleted", 0);
        queryWrapper.eq("tep.status", ExamPlanStatusEnum.STARTED.getValue());
        // 查询当前机构信息
        Long orgId = orgMapper.getOrgId(TokenLocalThreadUtil.get().getUserId()).getId();
        queryWrapper.eq("org.id", orgId);

        super.sort(queryWrapper, pageQuery);

        IPage<ExamPlanDetailResp> page = baseMapper.selectOrgExamPlanPagegetClassExamStatsPage(new Page<>(pageQuery
                .getPage(), pageQuery.getSize()), queryWrapper, roadExamTypeId);

        PageResp<ExamPlanResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 确认考试成绩
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean scoreConfirmed(Long planId, Long classId) {
        // 校验考试计划
        PlanApplyClassDO planApplyClassDO = planApplyClassMapper.selectOne(new LambdaQueryWrapper<PlanApplyClassDO>()
                .eq(PlanApplyClassDO::getPlanId, planId)
                .eq(PlanApplyClassDO::getClassId, classId)
                .select(PlanApplyClassDO::getIsScoreConfirmed));
        ValidationUtils.throwIfNull(planApplyClassDO, "考试记录不存在");
        ValidationUtils.throwIf(ScoreConfirmStatusEnum.CONFIRMED.getValue()
                .equals(planApplyClassDO.getIsScoreConfirmed()), "该班级成绩已确认，无法重复确认");

        // 查询该班级下报了当前计划的所有人
        List<EnrollDO> classEnrollList = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
                .eq(EnrollDO::getExamPlanId, planId)
                .eq(EnrollDO::getClassId, classId)
                .eq(EnrollDO::getEnrollStatus, EnrollStatusConstant.COMPLETED)
                .select(EnrollDO::getUserId));
        ValidationUtils.throwIfEmpty(classEnrollList, "该班级暂无考试记录");

        List<Long> candidateIds = classEnrollList.stream().map(EnrollDO::getUserId).toList();

        // 查询考试记录
        List<ExamRecordsDO> records = examRecordsMapper.selectList(new LambdaQueryWrapper<ExamRecordsDO>()
                .eq(ExamRecordsDO::getPlanId, planId)
                .in(ExamRecordsDO::getCandidateId, candidateIds)
        );
        ValidationUtils.throwIfEmpty(records, "暂无考试记录");

        // 是否存在未录入考试结果
        ValidationUtils.throwIf(records.stream()
                .anyMatch(r -> ExamResultStatusEnum.NOT_ENTERED.getValue() == r
                        .getExamResultStatus()), "存在未录入成绩的考试记录，请先进行录入");

        // 是否存在实操 / 道路成绩未录入
        ValidationUtils.throwIf(records.stream()
                .anyMatch(r -> ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                        .equals(r.getRoadInputStatus()) || ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                        .equals(r.getOperInputStatus())), "存在实操或道路成绩未录入记录，请先进行录入");

        // 找出成绩合格的考试记录并生成证书信息
        List<Long> passRecordIds = records.stream()
                .filter(r -> ExamResultStatusEnum.PASSED.getValue() == r.getExamResultStatus())
                .map(ExamRecordsDO::getId)
                .toList();
        if (ObjectUtil.isNotEmpty(passRecordIds)) {
            GenerateReq generateReq = new GenerateReq();
            generateReq.setRecordIds(passRecordIds);
            generateReq.setPlanType(ExamPlanTypeEnum.WORKER.getValue());
            examRecordsService.generateQualificationCertificate(generateReq);
        }
        // 确认成绩（加状态条件，防并发）
        int updated = planApplyClassMapper.update(null, new LambdaUpdateWrapper<PlanApplyClassDO>()
                .set(PlanApplyClassDO::getIsScoreConfirmed, ScoreConfirmStatusEnum.CONFIRMED.getValue())
                .eq(PlanApplyClassDO::getPlanId, planId)
                .eq(PlanApplyClassDO::getClassId, classId)
                .eq(PlanApplyClassDO::getIsScoreConfirmed, ScoreConfirmStatusEnum.UNCONFIRMED.getValue()));

        ValidationUtils.throwIf(updated == 0, "成绩确认失败，请刷新后重试");

        return Boolean.TRUE;
    }

    /**
     * 随机生成监考员开考密码
     *
     * @return
     */
    private String generateExamPassword() {
        return RandomUtil.randomNumbers(6);
    }

    /**
     * 选择一个座位未满的考场
     * 每次尝试 +1，占位成功说明可用
     */
    private Long findAvailableClassroom(List<Long> classroomIds, Long planId) {
        for (Long classroomId : classroomIds) {
            if (classroomMapper.incrementEnrolledCount(classroomId, planId) > 0) {
                return classroomId;
            }
        }
        return null;
    }

    /**
     * 为单个考生生成准考证 PDF（附带错误兜底）
     */
    private String safeGenerateWorkerTicket(Long userId,
                                            String idCard,
                                            String nickname,
                                            String encryptedExamNo,
                                            Long classId,
                                            String className) {
        try {
            return candidateTicketReactiveService
                    .generateWorkerTicket(userId, idCard, encryptedExamNo, classId, className);
        } catch (Exception e) {
            throw new BusinessException("生成考生【" + nickname + "】的准考证失败，请稍后重试");
        }
    }
}