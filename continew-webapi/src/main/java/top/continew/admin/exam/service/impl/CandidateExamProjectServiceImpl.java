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
import top.continew.admin.exam.mapper.CandidateExamProjectMapper;
import top.continew.admin.exam.model.entity.CandidateExamProjectDO;
import top.continew.admin.exam.model.query.CandidateExamProjectQuery;
import top.continew.admin.exam.model.req.CandidateExamProjectReq;
import top.continew.admin.exam.model.resp.CandidateExamProjectDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamProjectResp;
import top.continew.admin.exam.service.CandidateExamProjectService;

/**
 * 考生-考试项目考试状态业务实现
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Service
@RequiredArgsConstructor
public class CandidateExamProjectServiceImpl extends BaseServiceImpl<CandidateExamProjectMapper, CandidateExamProjectDO, CandidateExamProjectResp, CandidateExamProjectDetailResp, CandidateExamProjectQuery, CandidateExamProjectReq> implements CandidateExamProjectService {}