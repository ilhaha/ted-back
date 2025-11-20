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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.mapper.CandidateExamPaperMapper;
import top.continew.admin.exam.mapper.ClassroomMapper;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.model.entity.CandidateExamPaperDO;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.admin.examconnect.service.QuestionBankService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;
import top.continew.admin.worker.model.entity.WorkerExamTicketDO;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamAsyncService {

    private final ExamPlanMapper examPlanMapper;

    private final ClassroomMapper classroomMapper;

    private final EnrollMapper enrollMapper;

    private final AESWithHMAC aesWithHMAC;

    private final CandidateTicketService candidateTicketReactiveService;

    private final WorkerExamTicketMapper workerExamTicketMapper;

    private final UserMapper userMapper;

    private final QuestionBankService questionBankService;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 异步生成准考证号（中心主任确认后）
     */
    @Async
    public void generateAdmissionTicket(ExamPlanDO planDO) {

        final Long planId = planDO.getId();
        final String planYear = planDO.getPlanYear();

        try {
            log.info("【准考证任务启动】考试计划ID = {}", planId);

            // 1. 查询该计划所有考场
            List<Long> classroomIds = examPlanMapper.getPlanExamClassroom(planId);
            if (classroomIds == null || classroomIds.isEmpty()) {
                log.error("【跳过任务】考试计划 {} 未分配任何考场", planId);
                return;
            }
            Collections.shuffle(classroomIds, RANDOM);

            // 2. 查询报名记录
            List<EnrollDO> enrollList = enrollMapper.selectList(new LambdaQueryWrapper<EnrollDO>()
                .eq(EnrollDO::getExamPlanId, planId)
                .eq(EnrollDO::getIsDeleted, false));
            if (enrollList.isEmpty()) {
                log.warn("【跳过任务】考试计划 {} 无报名考生", planId);
                return;
            }

            List<WorkerExamTicketDO> ticketSaveList = new ArrayList<>();
            List<CandidateExamPaperDO> candidateExamPaperDOList = new ArrayList<>();

            // ===========================
            // 3. 按报名记录逐个生成准考证
            // ===========================
            for (EnrollDO enroll : enrollList) {

                Long classroomId = findAvailableClassroom(classroomIds, planId);
                if (classroomId == null) {
                    log.error("【终止任务】计划 {} 所有考场已满，剩余考生未能分配考场", planId);
                    break;
                }

                Integer seatNo = classroomMapper.getSeatNumber(classroomId, planId);
                if (seatNo == null) {
                    log.error("【跳过考生】考场 {} 无可用座位号", classroomId);
                    continue;
                }

                String seatStr = String.format("%03d", seatNo);
                String classroomStr = String.format("%03d", classroomId);
                String randomStr = String.format("%04d", RANDOM.nextInt(10000));

                // 最终准考证号
                String examNumber = planYear + randomStr + classroomStr + seatStr;

                // 更新报名表
                EnrollDO upd = new EnrollDO();
                upd.setId(enroll.getId());
                upd.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
                upd.setSeatId(Long.valueOf(seatStr));
                upd.setClassroomId(classroomId);
                enrollMapper.updateById(upd);

                // 查询用户
                UserDO user = userMapper.selectById(enroll.getUserId());
                if (user == null) {
                    log.warn("【跳过考生】报名记录 {} 的用户不存在", enroll.getId());
                    continue;
                }

                // 调用确实耗时的方法：生成 PDF
                String ticketUrl = safeGenerateWorkerTicket(enroll.getUserId(), user.getUsername(), upd
                    .getExamNumber(), enroll.getClassId());

                // 保存准考证表
                WorkerExamTicketDO ticket = new WorkerExamTicketDO();
                ticket.setCandidateName(user.getNickname());
                ticket.setEnrollId(enroll.getId());
                ticket.setTicketUrl(ticketUrl);

                ticketSaveList.add(ticket);

                ObjectMapper objectMapper = new ObjectMapper();
                ExamPaperVO examPaperVO = questionBankService.generateExamQuestionBank(planId);
                CandidateExamPaperDO candidateExamPaperDO = new CandidateExamPaperDO();
                candidateExamPaperDO.setPaperJson(objectMapper.writeValueAsString(examPaperVO));
                candidateExamPaperDO.setEnrollId(enroll.getId());
                candidateExamPaperDOList.add(candidateExamPaperDO);
            }

            // 4. 批量保存准考证
            if (!ticketSaveList.isEmpty()) {
                workerExamTicketMapper.insertBatch(ticketSaveList);
            }

            if (!candidateExamPaperDOList.isEmpty()) {
                candidateExamPaperMapper.insertBatch(candidateExamPaperDOList);
            }

            log.info("【准考证生成完成】考试计划 {}，成功处理 {} 条报名记录", planId, ticketSaveList.size());

        } catch (Exception e) {
            log.error("【准考证异步任务异常】计划 {}，错误：{}", planId, e.getMessage(), e);
        }
    }

    /**
     * 为单个考生生成准考证 PDF（附带错误兜底）
     */
    private String safeGenerateWorkerTicket(Long userId, String idCard, String encryptedExamNo, Long classId) {
        try {
            return candidateTicketReactiveService.generateWorkerTicket(userId, idCard, encryptedExamNo, classId);
        } catch (Exception e) {
            log.error("【PDF生成失败】用户ID={} 身份证={} 考试号={}，失败原因：{}", userId, idCard, encryptedExamNo, e.getMessage());
            return null;
        }
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
}
