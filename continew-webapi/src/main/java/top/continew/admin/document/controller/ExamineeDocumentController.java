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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.document.model.query.DocumentQuery;
import top.continew.admin.document.model.query.ExamineeDocumentQuery;
import top.continew.admin.document.model.req.ExamineeDocumentReq;
import top.continew.admin.document.model.req.StudentUploadDocumentsReq;
import top.continew.admin.document.model.resp.DocumentCandidatesResp;
import top.continew.admin.document.model.resp.ExamineeDocumentDetailResp;
import top.continew.admin.document.model.resp.ExamineeDocumentResp;
import top.continew.admin.document.service.DocumentService;
import top.continew.admin.document.service.ExamineeDocumentService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

/**
 * 考生资料关系管理 API
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Tag(name = "考生资料关系管理 API")
@RestController
@CrudRequestMapping(value = "/document/examineeDocument", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ExamineeDocumentController extends BaseController<ExamineeDocumentService, ExamineeDocumentResp, ExamineeDocumentDetailResp, ExamineeDocumentQuery, ExamineeDocumentReq> {

    @Resource
    private ExamineeDocumentService examineeDocumentService;

    @Resource
    private DocumentService documentService;

    @Operation(summary = "考生上传资料", description = "考生上传资料")
    @PostMapping({"/studentUploadDocuments"})
    public R studentUploadDocuments(@RequestBody StudentUploadDocumentsReq studentUploadDocumentsReq) {
        return examineeDocumentService.studentUploadDocuments(studentUploadDocumentsReq);
    }

    @Operation(summary = "考生查看资料", description = "考生查看资料")
    @GetMapping({"/listDocument"})
    public PageResp<DocumentCandidatesResp> listDocument(@Validated DocumentQuery query,
                                                         @Validated PageQuery pageQuery) {
        return documentService.listDocument(query, pageQuery);
    }

    @Operation(summary = "考生重新上传资料", description = "考生重新上传资料")
    @PostMapping({"/studentReUploadDocument"})
    public R studentReUploadDocument(@RequestBody StudentUploadDocumentsReq studentUploadDocumentsReq) {
        return examineeDocumentService.studentReUploadDocument(studentUploadDocumentsReq);
    }
}