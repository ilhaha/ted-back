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

import top.continew.admin.invigilate.model.enums.InvigilateStatus;
import top.continew.admin.statemachine.invigilate.Context;
import top.continew.admin.statemachine.invigilate.ExamPlanState;
import top.continew.admin.statemachine.invigilate.context.BatchEntryContext;
import top.continew.admin.statemachine.invigilate.context.BatchReviewContext;

public abstract class AbstractExamPlanState implements ExamPlanState {

    /**
     * 统一事件处理入口
     */
    @Override
    public void handleEvent(Context context) {
        if (context instanceof BatchEntryContext) {
            handleBatchEntryResult((BatchEntryContext)context);
        } else if (context instanceof BatchReviewContext) {
            handleBatchReviewResults((BatchReviewContext)context);
        } else {
            throw new IllegalArgumentException("未知上下文类型: " + context.getClass());
        }
    }

    /**
     * 默认实现：抛出不支持操作异常
     */
    @Override
    public void handleBatchEntryResult(BatchEntryContext context) {
        throw new UnsupportedOperationException("成绩录入不支持在状态 [%s] 下操作".formatted(getStatusCode()));
    }

    /**
     * 默认实现：抛出不支持操作异常
     */
    @Override
    public void handleBatchReviewResults(BatchReviewContext context) {
        throw new UnsupportedOperationException("审核结果处理不支持在状态 [%s] 下操作".formatted(getStatusCode()));
    }

    /**
     * 抽象方法：强制子类必须实现状态码
     */
    @Override
    public abstract InvigilateStatus getStatusCode();
}