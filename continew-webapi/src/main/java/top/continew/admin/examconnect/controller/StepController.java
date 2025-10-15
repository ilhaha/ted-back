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

package top.continew.admin.examconnect.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.examconnect.model.query.StepQuery;
import top.continew.admin.examconnect.model.req.StepReq;
import top.continew.admin.examconnect.model.resp.StepDetailResp;
import top.continew.admin.examconnect.model.resp.StepResp;
import top.continew.admin.examconnect.service.StepService;

import java.util.List;

/**
 * 步骤，存储题目的不同回答步骤管理 API
 *
 * @author Anton
 * @since 2025/04/07 10:42
 */
@Tag(name = "步骤，存储题目的不同回答步骤管理 API")
@RestController
@CrudRequestMapping(value = "/examconnect/step", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class StepController extends BaseController<StepService, StepResp, StepDetailResp, StepQuery, StepReq> {

    /**
     * 根据问题ID获取问题全部选项
     * 
     * @param questionId
     * @return
     */
    @GetMapping("/by/questionId/{questionId}")
    public List<StepResp> getListByQuestionId(@PathVariable("questionId") Long questionId) {
        return baseService.getListByQuestionId(questionId);
    }
}