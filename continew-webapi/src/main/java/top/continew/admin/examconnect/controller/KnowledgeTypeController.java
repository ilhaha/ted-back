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
import top.continew.admin.examconnect.model.query.KnowledgeTypeQuery;
import top.continew.admin.examconnect.model.req.KnowledgeTypeReq;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeDetailResp;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeResp;
import top.continew.admin.examconnect.service.KnowledgeTypeService;

/**
 * 知识类型，存储不同类型的知识占比管理 API
 *
 * @author Anton
 * @since 2025/04/07 10:39
 */
@Tag(name = "知识类型，存储不同类型的知识占比管理 API")
@RestController
@CrudRequestMapping(value = "/examconnect/knowledgeType", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class KnowledgeTypeController extends BaseController<KnowledgeTypeService, KnowledgeTypeResp, KnowledgeTypeDetailResp, KnowledgeTypeQuery, KnowledgeTypeReq> {}