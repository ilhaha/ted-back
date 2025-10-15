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

package top.continew.admin.statemachine.invigilate.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.continew.admin.invigilate.model.enums.InvigilateStatus;
import top.continew.admin.statemachine.invigilate.StateRegistry;
import top.continew.admin.statemachine.invigilate.context.BatchEntryContext;

import java.util.Objects;

import static top.continew.admin.invigilate.model.enums.InvigilateStatus.PENDING_ENTRY;
import static top.continew.admin.invigilate.model.enums.InvigilateStatus.PENDING_REVIEW;

/**
 * 待录入状态机
 */
@Component
@RequiredArgsConstructor
public class PendingEntryState extends AbstractExamPlanState {

    public final StateRegistry stateRegistry;
    public static final InvigilateStatus invigilateStatus = PENDING_ENTRY;

    @Override
    public void handleBatchEntryResult(BatchEntryContext context) {
        //1. 检查是否完成录入
        Long total = context.getMapper().queryHowMuchCandidates(context.getPlanId());
        Long recorded = context.getMapper().queryHowMuchGradesRecords(context.getPlanId());
        //1.1如果审核人数等于实际参与考试人数 则代表录入完成，转入待审核状态
        if (Objects.equals(recorded, total)) {
            context.transitionTo(stateRegistry.getState(PENDING_REVIEW));
        }
    }

    @Override
    public InvigilateStatus getStatusCode() {
        return invigilateStatus;
    }
}