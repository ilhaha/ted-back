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

package top.continew.admin.worker.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;
import top.continew.admin.worker.model.entity.WorkerExamTicketDO;
import top.continew.admin.worker.model.query.WorkerExamTicketQuery;
import top.continew.admin.worker.model.req.WorkerExamTicketReq;
import top.continew.admin.worker.model.resp.WorkerExamTicketDetailResp;
import top.continew.admin.worker.model.resp.WorkerExamTicketResp;
import top.continew.admin.worker.service.WorkerExamTicketService;

/**
 * 作业人员准考证业务实现
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Service
@RequiredArgsConstructor
public class WorkerExamTicketServiceImpl extends BaseServiceImpl<WorkerExamTicketMapper, WorkerExamTicketDO, WorkerExamTicketResp, WorkerExamTicketDetailResp, WorkerExamTicketQuery, WorkerExamTicketReq> implements WorkerExamTicketService {}