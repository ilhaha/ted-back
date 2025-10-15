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
import top.continew.admin.exam.mapper.ProjectDocumentTypeMapper;
import top.continew.admin.exam.model.entity.ProjectDocumentTypeDO;
import top.continew.admin.exam.model.query.ProjectDocumentTypeQuery;
import top.continew.admin.exam.model.req.ProjectDocumentTypeReq;
import top.continew.admin.exam.model.resp.ProjectDocumentTypeDetailResp;
import top.continew.admin.exam.model.resp.ProjectDocumentTypeResp;
import top.continew.admin.exam.service.ProjectDocumentTypeService;

/**
 * 项目与资料类型关联业务实现
 *
 * @author Anton
 * @since 2025/03/14 11:55
 */
@Service
@RequiredArgsConstructor
public class ProjectDocumentTypeServiceImpl extends BaseServiceImpl<ProjectDocumentTypeMapper, ProjectDocumentTypeDO, ProjectDocumentTypeResp, ProjectDocumentTypeDetailResp, ProjectDocumentTypeQuery, ProjectDocumentTypeReq> implements ProjectDocumentTypeService {

}