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

package top.continew.admin.exam.schedule;

import cn.crane4j.core.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.exam.mapper.CandidateExamProjectMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.ExamRecordsMapper;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.entity.CandidateExamProjectDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ExamRecordsDO;
import top.continew.admin.exam.model.entity.LicenseCertificateDO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class CertificateExpireScheduler {

    @Resource
    private LicenseCertificateMapper licenseCertificateMapper;
    @Resource
    private ExamRecordsMapper examRecordsMapper;
    @Resource
    private ExamPlanMapper examPlanMapper;
    @Resource
    private CandidateExamProjectMapper candidateExamProjectMapper;

    /**
     * 每天凌晨 1 点处理证书过期
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void execute() {

        log.info("【证书过期处理】任务开始");

        //  查询已过期、已生效的证书
        List<LicenseCertificateDO> expiredCertList = licenseCertificateMapper
            .selectList(new LambdaQueryWrapper<LicenseCertificateDO>().eq(LicenseCertificateDO::getCertGenerated, 1)
                .lt(LicenseCertificateDO::getEndDate, LocalDate.now())
                .eq(LicenseCertificateDO::getIsDeleted, false)
                .select(LicenseCertificateDO::getCandidateId, LicenseCertificateDO::getRecordId));

        if (CollectionUtils.isEmpty(expiredCertList)) {
            log.info("【证书过期处理】无过期证书，任务结束");
            return;
        }

        //  recordId → ExamRecords → planId
        Set<Long> recordIds = expiredCertList.stream()
            .map(LicenseCertificateDO::getRecordId)
            .collect(Collectors.toSet());

        Map<Long, ExamRecordsDO> recordMap = examRecordsMapper.selectBatchIds(recordIds)
            .stream()
            .collect(Collectors.toMap(ExamRecordsDO::getId, r -> r));

        //  planId → ExamPlan → projectId
        Set<Long> planIds = recordMap.values().stream().map(ExamRecordsDO::getPlanId).collect(Collectors.toSet());

        Map<Long, ExamPlanDO> planMap = examPlanMapper.selectBatchIds(planIds)
            .stream()
            .collect(Collectors.toMap(ExamPlanDO::getId, p -> p));

        // 组装 candidateId + projectId，去重
        Set<String> handledKeySet = new HashSet<>();

        int resetCount = 0;

        for (LicenseCertificateDO cert : expiredCertList) {

            ExamRecordsDO record = recordMap.get(cert.getRecordId());
            if (record == null) {
                continue;
            }

            ExamPlanDO plan = planMap.get(record.getPlanId());
            if (plan == null || plan.getExamProjectId() == null) {
                continue;
            }

            Long candidateId = cert.getCandidateId();
            Long projectId = plan.getExamProjectId();

            String key = candidateId + "_" + projectId;
            if (!handledKeySet.add(key)) {
                continue; // 同一考生同一项目只处理一次
            }

            //  重置 CandidateExamProject（进入新一轮）
            int updated = candidateExamProjectMapper.update(null, new LambdaUpdateWrapper<CandidateExamProjectDO>()
                .eq(CandidateExamProjectDO::getCandidateId, candidateId)
                .eq(CandidateExamProjectDO::getProjectId, projectId)
                .eq(CandidateExamProjectDO::getPassed, 1) // 幂等
                .set(CandidateExamProjectDO::getPassed, 0)
                .set(CandidateExamProjectDO::getUsedMakeup, 0)
                .setSql("attempt_no = attempt_no + 1")
                .set(CandidateExamProjectDO::getPassTime, null)
                .set(CandidateExamProjectDO::getCertificateExpireTime, null));

            resetCount += updated;
        }

        log.info("【证书过期处理】任务完成，重置项目轮次数量：{}", resetCount);
    }
}