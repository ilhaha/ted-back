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

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.document.model.req.DocumentAuditReq;
import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.admin.document.model.resp.DocumentTypeAddResp;
import top.continew.admin.document.model.vo.DocumentTypeNameVO;
import top.continew.admin.document.service.DocumentTypeService;
import top.continew.admin.util.Result;
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
import top.continew.starter.web.model.R;

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

    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    @SaIgnore
    @PostMapping("/qrcode/upload")
    public Boolean qrcodeUpload(@Validated @RequestBody QrcodeUploadReq qrcodeUploadReq) {
        return baseService.qrcodeUpload(qrcodeUploadReq);
    }

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
    /**
     * 审核资料接口
     * @param request 审核请求参数
     * @return 操作结果
     */
    @PostMapping("/audit")
    @Operation(summary = "审核资料接口")
    public R<Boolean> auditDocument(@RequestBody @Validated DocumentAuditReq request) {
        boolean result = documentService.auditDocument(request);
        return R.ok(result);
    }
}