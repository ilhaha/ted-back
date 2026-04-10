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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.service.ExamAsyncErrorLogService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamAsyncService {

    private final ExamTxService examTxService;

    private final ExamAsyncErrorLogService examAsyncErrorLogService;

    @Async("examExecutor")
    public void handleExamArrangementAsync(ExamPlanDO plan,
                                           ProjectDO project,
                                           List<EnrollDO> enrollList,
                                           List<Long> classroomIds,
                                           Boolean isTheory) {
        try {
            // 走代理 → 事务生效
            examTxService.doHandleExamArrangement(plan, project, enrollList, classroomIds, isTheory);
        } catch (Exception e) {
            examAsyncErrorLogService.recordError(plan.getId(), null, "计划确认级别错误", e);
        }
    }
}