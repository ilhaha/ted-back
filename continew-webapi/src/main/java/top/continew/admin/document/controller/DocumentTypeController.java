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

package top.continew.admin.document.controller;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import top.continew.admin.document.service.cache.DocumentTypeCache;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.document.model.query.DocumentTypeQuery;
import top.continew.admin.document.model.req.DocumentTypeReq;
import top.continew.admin.document.model.resp.DocumentTypeDetailResp;
import top.continew.admin.document.model.resp.DocumentTypeResp;
import top.continew.admin.document.service.DocumentTypeService;

/**
 * 资料类型主管理 API
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Tag(name = "资料类型主管理 API")
@RestController
@CrudRequestMapping(value = "/document/documentType", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class DocumentTypeController extends BaseController<DocumentTypeService, DocumentTypeResp, DocumentTypeDetailResp, DocumentTypeQuery, DocumentTypeReq> {
    @Resource
    private DocumentTypeService documentTypeService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private DocumentTypeCache documentTypeCache;

}