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

package top.continew.admin.worker.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.worker.model.query.WorkerExamTicketQuery;
import top.continew.admin.worker.model.req.WorkerExamTicketReq;
import top.continew.admin.worker.model.resp.WorkerExamTicketDetailResp;
import top.continew.admin.worker.model.resp.WorkerExamTicketResp;
import top.continew.admin.worker.service.WorkerExamTicketService;

/**
 * 作业人员准考证管理 API
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Tag(name = "作业人员准考证管理 API")
@RestController
@CrudRequestMapping(value = "/worker/workerExamTicket", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class WorkerExamTicketController extends BaseController<WorkerExamTicketService, WorkerExamTicketResp, WorkerExamTicketDetailResp, WorkerExamTicketQuery, WorkerExamTicketReq> {}