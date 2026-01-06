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

package top.continew.admin.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.model.resp.ImportResultVO;
import top.continew.admin.exam.model.entity.PersonQualificationDO;
import top.continew.admin.exam.model.req.PersonQualificationAuditReq;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.PersonQualificationQuery;
import top.continew.admin.exam.model.req.PersonQualificationReq;
import top.continew.admin.exam.model.resp.PersonQualificationDetailResp;
import top.continew.admin.exam.model.resp.PersonQualificationResp;
import top.continew.admin.exam.service.PersonQualificationService;

import java.util.List;

/**
 * 人员复审信息表管理 API
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Tag(name = "人员复审信息表管理 API")
@RestController
@CrudRequestMapping(value = "/exam/personQualification", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class PersonQualificationController extends BaseController<PersonQualificationService, PersonQualificationResp, PersonQualificationDetailResp, PersonQualificationQuery, PersonQualificationReq> {

    /**
     * 批量添加
     * 
     * @param reqs
     */
    @PostMapping("/batch/add")
    public Boolean batchAdd(@RequestBody List<PersonQualificationReq> reqs) {
        return baseService.batchAdd(reqs);
    }

    /**
     * 解析Excel
     * 
     * @param file
     * @return
     */
    @PostMapping("/analysis/excel")
    public ImportResultVO<PersonQualificationDO> analysisExcel(@RequestPart("file") MultipartFile file) {
        return baseService.analysisExcel(file);
    }

    /**
     * 批量审核
     * 
     * @param ids
     */
    @PostMapping("/batch/audit")
    public Boolean batchAudit(@RequestBody List<Long> ids) {
        return baseService.batchAudit(ids);
    }

    @Operation(summary = "批量导入复审人员信息")
    @PostMapping("/import/excel")
    public Boolean importExcel(@RequestPart("file") MultipartFile file) {
        baseService.importExcel(file);
        return true;
    }

    @PostMapping("/audit")
    public void audit(@RequestBody PersonQualificationAuditReq req) {
        baseService.audit(req);
    }

}