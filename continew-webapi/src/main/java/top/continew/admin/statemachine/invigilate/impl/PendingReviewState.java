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
import top.continew.admin.statemachine.invigilate.context.BatchReviewContext;

import java.util.Objects;
import static top.continew.admin.invigilate.model.enums.InvigilateStatus.COMPLETED;

/**
 * 待审核状态机
 */
@Component
@RequiredArgsConstructor
public class PendingReviewState extends AbstractExamPlanState {

    public final StateRegistry stateRegistry;

    public static InvigilateStatus invigilateStatus = InvigilateStatus.PENDING_REVIEW;

    @Override
    public void handleBatchReviewResults(BatchReviewContext context) {
        //1 查询参加考试的人数
        Long candidatesNumber = context.getMapper().queryHowMuchCandidates(context.getPlanId());
        //2 查询已经审核了多少人
        Long gradesRecords = context.getMapper().queryHowMuchGradesReview(context.getPlanId());
        //3 查询是否有审核不通过的考生成绩记录
        Long rejectReviewCount = context.getMapper().queryHowMuchReviewReject(context.getPlanId());
        //3判断应该转变成哪个状态
        if (Objects.equals(gradesRecords, candidatesNumber)) {
            //3.1考生成绩记录全部审核完成,转变成已完成
            context.transitionTo(stateRegistry.getState(COMPLETED));
        } else if (rejectReviewCount != 0) {
            //3.2 存在考生成绩记录审核不通过，转变为待录入
            context.transitionTo(stateRegistry.getState(invigilateStatus));
        }
    }

    @Override
    public InvigilateStatus getStatusCode() {
        return invigilateStatus;
    }
}
