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
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.invigilate.model.req.ExamScoreSubmitReq;
import top.continew.admin.invigilate.model.resp.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.admin.common.controller.BaseController;
import top.continew.admin.invigilate.model.query.PlanInvigilateQuery;
import top.continew.admin.invigilate.model.req.PlanInvigilateReq;
import top.continew.admin.invigilate.service.PlanInvigilateService;

import java.util.List;

/**
 * 考试计划监考人员关联管理 API
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
@Tag(name = "考试计划监考人员关联管理 API")
@RestController
@RequestMapping("/invigilate")
@Slf4j
public class PlanInvigilateController extends BaseController<PlanInvigilateService, PlanInvigilateResp, PlanInvigilateDetailResp, PlanInvigilateQuery, PlanInvigilateReq> {

    /**
     * 更换监考员
     * @param req
     * @return
     */
    @PostMapping("/replace")
    public Boolean replace(@Validated @RequestBody PlanInvigilateReq req) {
        return baseService.replace(req);
    }

    /**
     * 监考员无法参加监考
     * @param planId
     * @return
     */
    @PostMapping("/rejected/{planId}")
    public Boolean rejected(@PathVariable("planId") Long planId) {
        return baseService.rejected(planId);
    }

    /**
     * 根据计划id获取计划分配的监考员信息
     * @param planId
     * @return
     */
    @GetMapping("/by/planId/{planId}")
    public List<InvigilatorAssignResp> getListByPlanId(@PathVariable("planId") Long planId) {
        return baseService.getListByPlanId(planId);
    }

    /**
     * 根据监考人员Id查询
     * 
     * @param pageSize
     * @param currentPage
     * @param invigilateStatus
     * @return
     */
    @GetMapping("/byPlanInvigilatorId")
    public ExamRespList pageByInvigilatorId(@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                                            @RequestParam(name = "invigilateStatus", required = true) Integer invigilateStatus) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long invigilatorId = userTokenDo.getUserId();
        ExamRespList examRespList = baseService
            .pageByInvigilatorId(invigilatorId, invigilateStatus, pageSize, currentPage);
        return examRespList;
    }

    /**
     * 侧边详情栏信息
     * 
     * @param examId
     * @return
     */
    @GetMapping("/getInvigilateDetail")
    public InvigilateExamDetailResp getExamDetails(@RequestParam Long examId) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long invigilatorId = userTokenDo.getUserId();
        return baseService.getInvigilateExamDetail(invigilatorId, examId);
    }

    /**
     * 获取开考密码
     * 
     * @param examId
     * @return
     */
    @GetMapping("/getPassword")
    public String getPassword(@RequestParam Long examId) {

        return baseService.getPassword(examId);
    }

    /**
     * 批量更新考生成绩(监考端老师，机构端提交成绩）
     * 
     * @param req
     * @return
     */
    @Operation(summary = "批量提交考生成绩")
    @PostMapping("/scores/submit")
    public void submitScores(@RequestBody ExamScoreSubmitReq req) {
        baseService.enterGrades(req);
    }
}
