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

package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamNoticePlanMapper;
import top.continew.admin.exam.model.entity.ExamNoticePlanDO;
import top.continew.admin.exam.model.query.ExamNoticePlanQuery;
import top.continew.admin.exam.model.req.ExamNoticePlanReq;
import top.continew.admin.exam.model.resp.ExamNoticePlanDetailResp;
import top.continew.admin.exam.model.resp.ExamNoticePlanResp;
import top.continew.admin.exam.service.ExamNoticePlanService;

/**
 * 考试通知与考试计划关联业务实现
 *
 * @author ilhaha
 * @since 2026/04/14 15:21
 */
@Service
@RequiredArgsConstructor
public class ExamNoticePlanServiceImpl extends BaseServiceImpl<ExamNoticePlanMapper, ExamNoticePlanDO, ExamNoticePlanResp, ExamNoticePlanDetailResp, ExamNoticePlanQuery, ExamNoticePlanReq> implements ExamNoticePlanService {}