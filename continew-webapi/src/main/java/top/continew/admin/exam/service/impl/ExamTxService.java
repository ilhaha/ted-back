package top.continew.admin.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.ExamRecordConstants;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.config.WeldingConfig;
import top.continew.admin.exam.mapper.*;
import top.continew.admin.exam.model.dto.ExamPresenceDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.service.ExamAsyncErrorLogService;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.admin.examconnect.service.QuestionBankService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamTxService {

    private final EnrollMapper enrollMapper;

    private final CandidateExamPaperMapper candidateExamPaperMapper;

    private final QuestionBankService questionBankService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final AESWithHMAC aesWithHMAC;

    private final WorkerApplyMapper workerApplyMapper;

    private final UserMapper userMapper;

    private final WeldingOperScoreMapper weldingOperScoreMapper;

    private final WeldingConfig weldingConfig;

    private final ExamRecordsMapper examRecordsMapper;

    private final OrgClassCandidateMapper orgClassCandidateMapper;

    private final ExamPlanMapper examPlanMapper;

    private final ObjectMapper objectMapper;

    private final ExamAsyncErrorLogService examAsyncErrorLogService;

    @Value("${certificate.road-exam-type-id}")
    private Long roadExamTypeId;

    @Transactional(rollbackFor = Exception.class)
    public void doHandleExamArrangement(ExamPlanDO plan,
                                        ProjectDO project,
                                        List<EnrollDO> enrollList,
                                        List<Long> classroomIds,
                                        Boolean isTheory) {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        Map<Long, UserDO> userMap = getUserMap(enrollList);

        generateExamNumber(plan, project, enrollList, classroomIds, random);

        savePapers(plan, enrollList, userMap, isTheory);
    }

    // ===== 原方法直接搬过来 =====

    private void generateExamNumber(ExamPlanDO plan,
                                    ProjectDO project,
                                    List<EnrollDO> enrollList,
                                    List<Long> classroomIds,
                                    ThreadLocalRandom random) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String examDate = plan.getStartTime().format(formatter);
        String projectCode = project.getProjectCode();

        String planCountKey = RedisConstant.EXAM_PLAN_COUNT_KEY + projectCode + ":" + examDate;
        Long planCount = redisTemplate.opsForValue().increment(planCountKey);

        if (planCount != null && planCount == 1L) {
            redisTemplate.expire(planCountKey,
                    RedisConstant.EXAM_NUMBER_KEY_EXPIRE_DAYS,
                    TimeUnit.DAYS);
        }

        for (EnrollDO enroll : enrollList) {

            Long classroomId = classroomIds.get(random.nextInt(classroomIds.size()));

            String examNumber = projectCode
                    + planCount
                    + examDate
                    + String.format("%04d", enroll.getSeatId());

            try {
                enroll.setExamNumber(aesWithHMAC.encryptAndSign(examNumber));
            } catch (Exception e) {
                examAsyncErrorLogService.recordError(plan.getId(),enroll.getId(),"准考证级别错误",e);
                return;
            }
            enroll.setClassroomId(classroomId);
        }

        enrollMapper.updateBatchById(enrollList);
    }

    private void savePapers(ExamPlanDO plan,
                            List<EnrollDO> enrollList,
                            Map<Long, UserDO> userMap,
                            Boolean isTheory) {

        if (isTheory) {
            exemptTheory(plan.getId(), enrollList);
            return;
        }

        List<CandidateExamPaperDO> papers = enrollList.parallelStream()
                .map(enroll -> {
                    UserDO user = userMap.get(enroll.getUserId());
                    if (user == null) return null;

                    try {
                        ExamPaperVO vo = questionBankService.generateExamQuestionBank(plan.getId());

                        CandidateExamPaperDO paper = new CandidateExamPaperDO();
                        paper.setEnrollId(enroll.getId());
                        paper.setPaperJson(objectMapper.writeValueAsString(vo));
                        return paper;

                    } catch (Exception e) {
                        examAsyncErrorLogService.recordError(plan.getId(),enroll.getId(),"试卷级别错误",e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (!papers.isEmpty()) {
            candidateExamPaperMapper.insertBatch(papers);
        }
    }

//    private void savePapers(ExamPlanDO plan,
//                            List<EnrollDO> enrollList,
//                            Map<Long, UserDO> userMap,
//                            Boolean isTheory) {
//        // 只有实操考试
//        if (isTheory) {
//            exemptTheory(plan.getId(), enrollList);
//            return;
//        }
//        // 理论考试：生成试卷
//        List<CandidateExamPaperDO> papers = new ArrayList<>();
//        for (EnrollDO enroll : enrollList) {
//            UserDO user = userMap.get(enroll.getUserId());
//            if (user == null) continue;
//            try {
//                ExamPaperVO vo = questionBankService.generateExamQuestionBank(plan.getId());
//
//                CandidateExamPaperDO paper = new CandidateExamPaperDO();
//                paper.setEnrollId(enroll.getId());
//                paper.setPaperJson(objectMapper.writeValueAsString(vo));
//
//                papers.add(paper);
//
//            } catch (Exception e) {
//                continue;
//            }
//        }
//
//        if (!papers.isEmpty()) {
//            candidateExamPaperMapper.insertBatch(papers);
//        }
//    }

    private Map<Long, UserDO> getUserMap(List<EnrollDO> list) {
        List<Long> userIds = list.stream().map(EnrollDO::getUserId).distinct().toList();
        return userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(UserDO::getId, u -> u));
    }

    private void exemptTheory(Long planId, List<EnrollDO> enrollList) {

        // 2. 查询是否有实操/道路考试
        ExamPresenceDTO examPlanOperAndRoadDTO = examRecordsMapper.hasOperationOrRoadExam(planId, roadExamTypeId);
        boolean hasOper = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsOperation());
        boolean hasRoad = ProjectHasExamTypeEnum.YES.getValue().equals(examPlanOperAndRoadDTO.getIsRoad());

        // 3. 批量生成考试记录
        List<ExamRecordsDO> examRecordsList = enrollList.stream().map(item -> {
            ExamRecordsDO examRecordsDO = new ExamRecordsDO();
            examRecordsDO.setIsTheoryExempt(TheoryExemptEnum.EXEMPT.getValue());
            examRecordsDO.setPlanId(planId);
            examRecordsDO.setCandidateId(item.getUserId());
            examRecordsDO.setExamScores(ExamRecordConstants.PASSING_SCORE);
            examRecordsDO.setOperScores(hasOper ? 0 : ExamRecordConstants.PASSING_SCORE);
            examRecordsDO.setOperInputStatus(hasOper
                    ? ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                    : ExamScoreEntryStatusEnum.ENTERED.getValue());
            examRecordsDO.setRoadScores(0);
            examRecordsDO.setRoadInputStatus(hasRoad
                    ? ExamScoreEntryStatusEnum.NO_ENTRY.getValue()
                    : ExamScoreEntryStatusEnum.ENTERED.getValue());
            examRecordsDO.setAttemptType(ExamRecordAttemptEnum.FIRST.getValue());
            examRecordsDO.setExamResultStatus((hasOper || hasRoad)
                    ? ExamResultStatusEnum.NOT_ENTERED.getValue()
                    : ExamResultStatusEnum.PASSED.getValue());
            return examRecordsDO;
        }).toList();

        examRecordsMapper.insertBatch(examRecordsList);

        // 判断是否是焊接项目
        ExamPlanDO examPlanDO = examPlanMapper.selectById(planId);
        Long examProjectId = examPlanDO.getExamProjectId();
        List<Long> projectIdList = weldingConfig.getProjectIdList();
        if (projectIdList.contains(examProjectId)) {
            // 4. 批量生成焊接实操成绩
            insertBatchWeldingOperScore(examRecordsList);
        }

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

}