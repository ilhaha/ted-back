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

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import top.continew.admin.document.model.resp.DocumentTypeAddResp;
import top.continew.admin.document.model.vo.DocumentTypeNameVO;
import top.continew.admin.document.service.DocumentTypeService;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.document.model.query.DocumentQuery;
import top.continew.admin.document.model.req.DocumentReq;
import top.continew.admin.document.model.resp.DocumentDetailResp;
import top.continew.admin.document.model.resp.DocumentResp;
import top.continew.admin.document.service.DocumentService;

import java.util.List;

/**
 * 资料核心存储管理 API
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Tag(name = "资料核心存储管理 API")
@RestController
@CrudRequestMapping(value = "/document/document", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class DocumentController extends BaseController<DocumentService, DocumentResp, DocumentDetailResp, DocumentQuery, DocumentReq> {
    @Resource
    private DocumentTypeService documentTypeService;
    @Resource
    private DocumentService documentService;

    @GetMapping("/typeName")
    @Operation(summary = "获取资料种类名称列表")
    public List<DocumentTypeNameVO> getTypeNameList() {
        return documentTypeService.getDocumentTypeName();
    }

    @GetMapping("/getType")
    @Operation(summary = "获取资料种类")
    public List<DocumentTypeAddResp> getType() {
        return documentService.getDocumentType();
    }

    @PostMapping("/upload")
    @Operation(summary = "上传资料")
    public void uploadDocument(DocumentReq req) {
        documentService.upload(req);
    }
}