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

package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.ProjectTrainingMapper;
import top.continew.admin.training.model.entity.ProjectTrainingDO;
import top.continew.admin.training.model.query.ProjectTrainingQuery;
import top.continew.admin.training.model.req.ProjectTrainingReq;
import top.continew.admin.training.model.resp.ProjectTrainingDetailResp;
import top.continew.admin.training.model.resp.ProjectTrainingResp;
import top.continew.admin.training.service.ProjectTrainingService;

/**
 * 项目培训关联业务实现
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Service
@RequiredArgsConstructor
public class ProjectTrainingServiceImpl extends BaseServiceImpl<ProjectTrainingMapper, ProjectTrainingDO, ProjectTrainingResp, ProjectTrainingDetailResp, ProjectTrainingQuery, ProjectTrainingReq> implements ProjectTrainingService {}