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

package top.continew.admin.exam.controller;

import top.continew.admin.exam.model.vo.PlanLocationAndRoomVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamLocationQuery;
import top.continew.admin.exam.model.req.ExamLocationReq;
import top.continew.admin.exam.model.resp.ExamLocationDetailResp;
import top.continew.admin.exam.model.resp.ExamLocationResp;
import top.continew.admin.exam.service.ExamLocationService;

import java.util.List;

/**
 * 考试地点管理 API
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Tag(name = "考试地点管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examLocation", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ExamLocationController extends BaseController<ExamLocationService, ExamLocationResp, ExamLocationDetailResp, ExamLocationQuery, ExamLocationReq> {

    /**
     * 根据计划id获取计划对应的考试地点和考场信息
     * 
     * @param planId
     * @return
     */
    @GetMapping("/room/{planId}")
    public List<PlanLocationAndRoomVO> getPlanLocationAndRoomByPlanId(@PathVariable("planId") Long planId) {
        return baseService.getPlanLocationAndRoomByPlanId(planId);
    }
}