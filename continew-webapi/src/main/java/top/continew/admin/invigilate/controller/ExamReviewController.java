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

package top.continew.admin.invigilate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.invigilate.model.entity.Grades;
import top.continew.admin.invigilate.model.entity.TedExamRecords;
import top.continew.admin.invigilate.model.req.UpdateReviewReq;
import top.continew.admin.invigilate.service.PlanInvigilateService;

import java.util.List;

@Tag(name = "考试审核管理 API")
@RestController
@RequestMapping("/exam/review")
@RequiredArgsConstructor
public class ExamReviewController {

    private final PlanInvigilateService planInvigilateService;

    @Operation(summary = "更新审核状态")
    @PutMapping("/status")
    public void updateReviewStatus(@RequestParam Long examId, @RequestBody List<UpdateReviewReq> updateReviewReqs) {
        planInvigilateService.updateReviewStatus(examId, updateReviewReqs);
    }

    @Operation(summary = "获取待审核记录")
    @GetMapping("/records")
    public List<Grades> getReviewRecords(@RequestParam Long examId) {
        return planInvigilateService.queryNeedReviewByExamId(examId);
    }

    /**
     * 获取所有待录入和已录入和被拒绝的考试记录
     * 
     * @param examId
     * @return
     */
    @GetMapping("/scores/alreadyCommitOrReject")
    public List<Grades> queryAlreadyCommitOrReject(@RequestParam Long examId) {
        return planInvigilateService.queryAlreadyCommitOrReject(examId);
    }

    /**
     * 更新考试成绩记录
     * 
     * @param tedExamRecords
     */
    @PostMapping("/updateScoresRecord")
    public void updateScoresRecord(@RequestBody TedExamRecords tedExamRecords) {
        planInvigilateService.updateScoreRecord(tedExamRecords);
    }

    /**
     * 删除考试成绩记录
     * 
     * @param planId
     * @param candidateId
     */
    @PostMapping("/deleteScoresRecord")
    public void updateScoresRecord(@RequestParam Long planId, @RequestParam Long candidateId) {
        planInvigilateService.deleteScoreRecord(planId, candidateId);
    }
}