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

package top.continew.admin.invigilate.service.impl;

import cn.crane4j.core.util.StringUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.enums.InvigilateStatusEnum;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.config.ali.AliYunConfig;
import top.continew.admin.constant.SmsConstants;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.invigilate.constant.statueConstant;
import top.continew.admin.invigilate.mapper.ExamRecords1Mapper;
import top.continew.admin.invigilate.model.entity.Grades;
import top.continew.admin.invigilate.model.entity.TedExamRecords;
import top.continew.admin.invigilate.model.enums.ReviewStatus;
import top.continew.admin.invigilate.model.req.ExamScoreSubmitReq;
import top.continew.admin.invigilate.model.req.UpdateReviewReq;
import top.continew.admin.invigilate.model.resp.*;
import top.continew.admin.statemachine.invigilate.StateRegistry;
import top.continew.admin.statemachine.invigilate.ExamPlanState;
import top.continew.admin.statemachine.invigilate.context.BatchEntryContext;
import top.continew.admin.statemachine.invigilate.context.BatchReviewContext;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.invigilate.mapper.PlanInvigilateMapper;
import top.continew.admin.invigilate.model.entity.PlanInvigilateDO;
import top.continew.admin.invigilate.model.query.PlanInvigilateQuery;
import top.continew.admin.invigilate.model.req.PlanInvigilateReq;
import top.continew.admin.invigilate.service.PlanInvigilateService;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static top.continew.admin.invigilate.model.enums.InvigilateStatus.*;
import static top.continew.admin.invigilate.model.enums.ReviewStatus.*;

/**
 * 考试计划监考人员关联业务实现
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlanInvigilateServiceImpl extends BaseServiceImpl<PlanInvigilateMapper, PlanInvigilateDO, PlanInvigilateResp, PlanInvigilateDetailResp, PlanInvigilateQuery, PlanInvigilateReq> implements PlanInvigilateService {

    private final PlanInvigilateMapper planInvigilateMapper;
    private final ExamRecords1Mapper examRecords1Mapper;
    private final StateRegistry stateRegistry;
    private final UserMapper userMapper;
    private final ExamPlanMapper examPlanMapper;

    private final AsyncClient smsAsyncClient;

    @Autowired
    private final AliYunConfig smsConfig;

    @Override
    public ExamRespList pageByInvigilatorId(Long invigilatorId,
                                            Integer invigilateStatus,
                                            Integer pageSize,
                                            Integer currentPage) {
        //1.计算offset
        int offset = (currentPage - 1) * pageSize;
        //2.分页查询数据
        List<InvigilatorPlanResp> enrollResps = planInvigilateMapper
            .queryEnrollRespByInvigilatorIdAndInvigilateStatus(invigilatorId, invigilateStatus, pageSize, offset);
        //3.查询总条数
        Long total = planInvigilateMapper.queryTotal(invigilatorId, invigilateStatus);
        //4.返回数据
        ExamRespList examRespList = new ExamRespList();
        examRespList.setEnrollRespList(enrollResps);
        examRespList.setTotal(total);
        return examRespList;
    }

    @Override
    public InvigilateExamDetailResp getInvigilateExamDetail(Long invigilatorId, Long examId) {
        InvigilateExamDetailResp invigilateExamDetailResp = planInvigilateMapper.queryExamDetail(invigilatorId, examId);
        long examDuration = Duration.between(invigilateExamDetailResp.getStartTime(), invigilateExamDetailResp
            .getEndTime()).toMinutes();
        invigilateExamDetailResp.setExamDuration(examDuration);
        return invigilateExamDetailResp;
    }

    @Transactional
    @Override
    public void enterGrades(ExamScoreSubmitReq examScoreSubmitReq) {
        //1.判断是否是为待录入状态
        Long planId = examScoreSubmitReq.getExamPlanId();
        Long invigilateStatus = planInvigilateMapper.isInvigilateStatus(planId, PENDING_ENTRY.getCode());
        if (invigilateStatus == 0) {
            return;
        }
        //1.1判断需要录入是否为空
        if (examScoreSubmitReq.getScores() == null || examScoreSubmitReq.getScores().isEmpty()) {
            return;
        }
        //1.2判断是否超过了录入数量
        Long gradesRecords = planInvigilateMapper.queryHowMuchGradesRecords(planId);
        Long candidates = planInvigilateMapper.queryHowMuchCandidates(planId);
        if (gradesRecords + examScoreSubmitReq.getScores().size() > candidates) {
            return;
        }
        //2.批量更新考试成绩
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long userId = userTokenDo.getUserId();
        List<Grades> gradesList = new ArrayList<>();
        for (ExamScoreSubmitReq.ScoreItem scoreItem : examScoreSubmitReq.getScores()) {
            Grades grades = new Grades();
            grades.setPlanId(planId);
            grades.setCandidateId(scoreItem.getStudentId());
            grades.setExamScores(scoreItem.getScore());
            grades.setAnswerSheetUrl(scoreItem.getAnswerSheetUrl());
            gradesList.add(grades);
        }
        try {
            planInvigilateMapper.batchInsertOrUpdateGrades(gradesList);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一考生在同一考试计划中已存在记录");
        }
        //3.判断是否应该进入其他状态
        ExamPlanState pendingEntryStatemachine = stateRegistry.getState(PENDING_ENTRY);
        BatchEntryContext batchEntryContext = BatchEntryContext.builder()
            .planId(planId)
            .mapper(planInvigilateMapper)
            .currentState(pendingEntryStatemachine)
            .build();
        pendingEntryStatemachine.handleEvent(batchEntryContext);
    }

    //    private void isEnterAllScoresRecords(@NotBlank(message = "考试计划ID不能为空") Long planId) {
    //        //1 查询参加考试的人数
    //        Long candidatesNumber = planInvigilateMapper.queryHowMuchCandidates(planId);
    //        log.info("candidatesNumber:{}",candidatesNumber);
    //        //2 查询已经录入了多少人
    //        Long gradesRecords = planInvigilateMapper.queryHowMuchGradesRecords(planId);
    //        log.info("gradesRecords:{}",gradesRecords);
    //        //3判断是否已经全部录入
    //        if(gradesRecords >= candidatesNumber){
    //            planInvigilateMapper.updateInvigilateStatus(planId, PENDING_REVIEW.getCode());
    //        } else {
    //            planInvigilateMapper.updateInvigilateStatus(planId, PENDING_ENTRY.getCode());
    //        }
    //    }

    public void updateReviewStatus(Long examId, List<UpdateReviewReq> updateReviewReqs) {
        //1.参数校验
        if (examId == null || CollUtil.isEmpty(updateReviewReqs)) {
            throw new IllegalArgumentException("参数不能为空");
        }

        //2. 按状态分组：通过 vs 不通过
        Map<Boolean, List<UpdateReviewReq>> statusGroups = updateReviewReqs.stream()
            .collect(Collectors.groupingBy(req -> req.getNewStatus() == ReviewStatus.APPROVED.getCode()));

        //3. 处理审核通过的记录
        List<UpdateReviewReq> approvedList = statusGroups.getOrDefault(true, Collections.emptyList());
        if (!approvedList.isEmpty()) {
            batchUpdateStatus(examId, approvedList, ReviewStatus.APPROVED);

        }

        //4. 处理审核不通过的记录
        List<UpdateReviewReq> rejectedList = statusGroups.getOrDefault(false, Collections.emptyList());
        if (!rejectedList.isEmpty()) {
            batchUpdateStatus(examId, rejectedList, ReviewStatus.REJECTED);
            planInvigilateMapper.updateInvigilateStatus(examId, PENDING_ENTRY.getCode()); // 只需执行一次
            return;
        }

        //5.判断是否审核完成
        ExamPlanState pendingEntryStatemachine = stateRegistry.getState(PENDING_REVIEW);
        BatchReviewContext batchReviewContext = BatchReviewContext.builder()
            .planId(examId)
            .mapper(planInvigilateMapper)
            .currentState(pendingEntryStatemachine)
            .build();
        pendingEntryStatemachine.handleEvent(batchReviewContext);
    }

    // 批量更新状态
    private void batchUpdateStatus(Long examId, List<UpdateReviewReq> reqs, ReviewStatus status) {
        List<Long> candidateIds = reqs.stream().map(UpdateReviewReq::getCandidateId).collect(Collectors.toList());

        // 构建批量更新条件
        UpdateWrapper<TedExamRecords> wrapper = new UpdateWrapper<>();
        wrapper.set("review_status", status.getCode())
            .set("registration_progress", 3)
            .eq("plan_id", examId)
            .in("candidate_id", candidateIds)
            .eq("is_deleted", 0);
        log.info("wrapper:examId:{},candidate_id:{},sc:{}", examId, candidateIds, status.getCode());
        int update = examRecords1Mapper.update(null, wrapper);
        log.info("update:{}", update);
    }

    @Override
    public List<Grades> queryAlreadyCommitOrReject(Long examId) {
        return planInvigilateMapper.queryAlreadyCommitOrReject(examId);
    }

    @Override
    public List<Grades> queryNeedReviewByExamId(Long examId) {
        List<Grades> grades = planInvigilateMapper.queryNeedReviewByExamId(examId);
        log.info("res:{}", grades);
        return grades;
    }

    @Override
    public void updateScoreRecord(TedExamRecords tedExamRecords) {
        //1.只有待审核和已拒绝的成绩记录才能更新
        log.info("req:{}", tedExamRecords);
        QueryWrapper<TedExamRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id", tedExamRecords.getPlanId()).eq("candidate_id", tedExamRecords.getCandidateId());
        TedExamRecords tedExamRecordsTemp = examRecords1Mapper.selectOne(queryWrapper);
        Integer reviewStatus = tedExamRecordsTemp.getReviewStatus();
        //1.1 已通过，不能更改
        if (reviewStatus == APPROVED.getCode()) {
            return;
        }
        //2. 更新成绩记录
        tedExamRecords.setId(tedExamRecordsTemp.getId());
        tedExamRecords.setReviewStatus(0);
        examRecords1Mapper.updateById(tedExamRecords);

        //3. 判断是否全部录入
        ExamPlanState pendingEntryStatemachine = stateRegistry.getState(PENDING_ENTRY);
        BatchEntryContext batchEntryContext = BatchEntryContext.builder()
            .planId(tedExamRecordsTemp.getPlanId())
            .mapper(planInvigilateMapper)
            .currentState(pendingEntryStatemachine)
            .build();
        pendingEntryStatemachine.handleEvent(batchEntryContext);
    }

    public void deleteScoreRecord(Long examId, Long candidateId) {
        //1.只有待审核和已拒绝的成绩记录才能更新
        log.info("examId:{}, candidateId:{}", examId, candidateId);
        QueryWrapper<TedExamRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id", examId).eq("candidate_id", candidateId);
        TedExamRecords tedExamRecords = examRecords1Mapper.selectOne(queryWrapper);
        Integer reviewStatus = tedExamRecords.getReviewStatus();
        //1.1 已通过，不能更改
        if (reviewStatus == APPROVED.getCode()) {
            return;
        }
        //2. 删除记录
        examRecords1Mapper.deleteById(tedExamRecords.getId());
        //3.判断是否全部录入
        ExamPlanState pendingEntryStatemachine = stateRegistry.getState(PENDING_ENTRY);
        BatchEntryContext batchEntryContext = BatchEntryContext.builder()
            .planId(examId)
            .mapper(planInvigilateMapper)
            .currentState(pendingEntryStatemachine)
            .build();
        pendingEntryStatemachine.handleEvent(batchEntryContext);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getPassword(Long examId) {
        // 获取当前用户
        Long userId = TokenLocalThreadUtil.get().getUserId();

        // 1. 查询监考任务
        List<PlanInvigilateDO> invigilateList = baseMapper.selectList(new LambdaQueryWrapper<PlanInvigilateDO>()
            .eq(PlanInvigilateDO::getExamPlanId, examId));
        ValidationUtils.throwIfEmpty(invigilateList, "无监考任务");

        // 2. 获取当前监考员任务
        PlanInvigilateDO currentTask = planInvigilateMapper.selectByUserId(userId, examId);
        ValidationUtils.throwIfNull(currentTask, "未查询到监考任务");

        // 绑定手机号检查
        UserDO user = userMapper.selectById(userId);
        if (StringUtils.isEmpty(user.getPhone())) {
            return statueConstant.EXAM_PASSWORD_BIND_PHONE;
        }

        // 查询考试计划名称
        String examPlanName = examPlanMapper.selectById(examId).getExamPlanName();

        // 当前考场 ID
        Long classroomId = currentTask.getClassroomId();

        // 3. 查找当前考场是否已有监考员生成过密码（不同考场密码不同）
        String existPassword = invigilateList.stream()
            .filter(t -> t.getClassroomId().equals(classroomId))
            .map(PlanInvigilateDO::getExamPassword)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);

        // 需要生成或复用的密码
        String finalPassword = (existPassword != null) ? existPassword : RandomUtil.randomNumbers(6);

        // 4. 当前用户如果没有密码 → 写库
        if (currentTask.getExamPassword() == null) {
            updateExamPassword(userId, examId, classroomId, finalPassword);
        }

        // 5. 发送短信
        sendExamPwdSms(user.getPhone(), examPlanName, finalPassword);

        return statueConstant.EXAM_PASSWORD_SEND_CONTENT;
    }

    /**
     * 更新监考记录中的考试密码
     */
    private void updateExamPassword(Long userId, Long examId, Long classroomId, String pwd) {
        planInvigilateMapper.deductBalanceByIds(pwd, new QueryWrapper<PlanInvigilateDO>().eq("invigilator_id", userId)
            .eq("invigilate_status", InvigilateStatusEnum.TO_CONFIRM.getValue())
            .eq("exam_plan_id", examId)
            .eq("classroom_id", classroomId));
    }

    /**
     * 发送开考密码短信
     */
    private void sendExamPwdSms(String phone, String planName, String password) {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("PlanName", planName);
        paramsMap.put("code", password);

        SendSmsRequest request = SendSmsRequest.builder()
            .phoneNumbers(phone)
            .signName(smsConfig.getSignName())
            .templateCode(smsConfig.getTemplateCodes().get(SmsConstants.EXAM_NOTIFICATION_TEMPLATE))
            .templateParam(JSON.toJSONString(paramsMap))
            .build();

        smsAsyncClient.sendSms(request);
    }

    /**
     * 根据计划id获取计划分配的监考员信息
     *
     * @param planId
     * @return
     */
    @Override
    public List<InvigilatorAssignResp> getListByPlanId(Long planId) {
        return baseMapper.getListByPlanId(planId);
    }

    /**
     * 监考员无法参加监考
     *
     * @param planId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rejected(Long planId) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        return baseMapper.update(new LambdaUpdateWrapper<PlanInvigilateDO>().eq(PlanInvigilateDO::getExamPlanId, planId)
            .eq(PlanInvigilateDO::getInvigilatorId, userTokenDo.getUserId())
            .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.REJECTED.getValue())) > 0;
    }

    /**
     * 更换监考员
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean replace(PlanInvigilateReq req) {
        Long planInvigilateId = req.getId();
        PlanInvigilateDO planInvigilateDO = baseMapper.selectById(planInvigilateId);
        ValidationUtils.throwIfNull(planInvigilateDO, "未查询到记录");
        return baseMapper.update(new LambdaUpdateWrapper<PlanInvigilateDO>()
            .eq(PlanInvigilateDO::getId, planInvigilateId)
            .set(PlanInvigilateDO::getInvigilatorId, req.getInvigilateId())
            .set(PlanInvigilateDO::getInvigilateStatus, InvigilateStatusEnum.TO_CONFIRM.getValue())) > 0;
    }

}
