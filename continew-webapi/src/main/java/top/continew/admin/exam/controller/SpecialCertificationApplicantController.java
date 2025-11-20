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

import net.dreamlu.mica.core.result.R;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.exam.model.dto.BatchAuditSpecialCertificationApplicantDTO;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantListReq;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.SpecialCertificationApplicantQuery;
import top.continew.admin.exam.model.req.SpecialCertificationApplicantReq;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantDetailResp;
import top.continew.admin.exam.model.resp.SpecialCertificationApplicantResp;
import top.continew.admin.exam.service.SpecialCertificationApplicantService;

/**
 * 特种设备人员资格申请管理 API
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Tag(name = "特种设备人员资格申请管理 API")
@RestController
@CrudRequestMapping(value = "/exam/specialCertificationApplicant", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class SpecialCertificationApplicantController extends BaseController<SpecialCertificationApplicantService, SpecialCertificationApplicantResp, SpecialCertificationApplicantDetailResp, SpecialCertificationApplicantQuery, SpecialCertificationApplicantReq> {

    /**
     * 根据当前登录的考生获取特种设备人员资格申请表
     * 
     * @return
     */
    @GetMapping("/candidates/{planId}")
    public SpecialCertificationApplicantResp getByCandidates(@PathVariable("planId") Long planId,
                                                             @RequestParam(value = "applySource", required = false) Integer applySource) {
        return baseService.getByCandidates(planId, applySource);
    }

    /**
     * 考生上传特种设备人员资格申请表
     * 
     * @return
     */
    @PostMapping("/candidates/upload")
    public Boolean candidatesUpload(@Validated @RequestBody SpecialCertificationApplicantReq specialCertificationApplicantReq) {
        return baseService.candidatesUpload(specialCertificationApplicantReq);
    }

    /**
     * 机构代替一个及多个考生上传特种设备人员资格申请表
     * 
     * @return
     */
    @PostMapping("/candidates/uploads")
    public Boolean candidatesUploads(@RequestBody SpecialCertificationApplicantListReq scar) {
        return baseService.candidatesUploads(scar);
    }

    @PutMapping("/batch-audit")
    public R batchAudit(@RequestBody BatchAuditSpecialCertificationApplicantDTO dto) {
        return baseService.batchAudit(dto);

    }

    /**
     * 审核自定义
     *
     */
    @PutMapping("/update/{id}")
    public R updateResult(@RequestBody SpecialCertificationApplicantReq req,
                          @PathVariable("id") Long id) throws Exception {
        return baseService.updateResult(req, id);
    }

}