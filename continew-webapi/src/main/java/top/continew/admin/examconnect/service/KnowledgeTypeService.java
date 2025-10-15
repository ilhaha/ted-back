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

package top.continew.admin.examconnect.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.examconnect.model.query.KnowledgeTypeQuery;
import top.continew.admin.examconnect.model.req.KnowledgeTypeReq;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeDetailResp;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeResp;

/**
 * 知识类型，存储不同类型的知识占比业务接口
 *
 * @author Anton
 * @since 2025/04/07 10:39
 */
public interface KnowledgeTypeService extends BaseService<KnowledgeTypeResp, KnowledgeTypeDetailResp, KnowledgeTypeQuery, KnowledgeTypeReq> {}