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

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamViolationQuery;
import top.continew.admin.exam.model.req.ExamViolationReq;
import top.continew.admin.exam.model.resp.ExamViolationDetailResp;
import top.continew.admin.exam.model.resp.ExamViolationResp;
import top.continew.admin.exam.service.ExamViolationService;

/**
 * 考试劳务费配置管理 API
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Tag(name = "考试劳务费配置管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examViolation", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ExamViolationController extends BaseController<ExamViolationService, ExamViolationResp, ExamViolationDetailResp, ExamViolationQuery, ExamViolationReq> {}