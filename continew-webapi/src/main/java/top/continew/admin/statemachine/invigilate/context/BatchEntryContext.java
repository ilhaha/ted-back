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

package top.continew.admin.statemachine.invigilate.context;

import lombok.Builder;
import lombok.Data;
import top.continew.admin.invigilate.mapper.PlanInvigilateMapper;
import top.continew.admin.statemachine.invigilate.Context;
import top.continew.admin.statemachine.invigilate.ExamPlanState;

/**
 * 上下文对象
 */
@Data
@Builder
public class BatchEntryContext implements Context {
    private ExamPlanState currentState;
    private Long planId;
    private PlanInvigilateMapper mapper;

    // 状态转移方法
    public void transitionTo(ExamPlanState newState) {
        this.currentState = newState;
        mapper.updateInvigilateStatus(planId, newState.getStatusCode().getCode());
    }
}