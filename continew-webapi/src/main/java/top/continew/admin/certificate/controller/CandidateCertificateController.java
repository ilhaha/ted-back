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

package top.continew.admin.certificate.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.certificate.model.dto.ReexaminationDTO;
import top.continew.admin.util.Result;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.certificate.model.query.CandidateCertificateQuery;
import top.continew.admin.certificate.model.req.CandidateCertificateReq;
import top.continew.admin.certificate.model.resp.CandidateCertificateDetailResp;
import top.continew.admin.certificate.model.resp.CandidateCertificateResp;
import top.continew.admin.certificate.service.CandidateCertificateService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 考生证件管理 API
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Tag(name = "考生证件管理 API")
@RestController
@Slf4j
@CrudRequestMapping(value = "/certificate/candidateCertificate", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class CandidateCertificateController extends BaseController<CandidateCertificateService, CandidateCertificateResp, CandidateCertificateDetailResp, CandidateCertificateQuery, CandidateCertificateReq> {
    @Resource
    private CandidateCertificateService candidateCertificateService;

    @GetMapping("/getCandidateCertificateList")
    public PageResp<CandidateCertificateResp> getCandidateCertificateList(@Validated CandidateCertificateQuery query,
                                                                          @Validated PageQuery pageQuery) {

        return candidateCertificateService.getCandidateCertificateList(query, pageQuery);
    }

    @GetMapping("/getUserCertificate")
    public List<CandidateCertificateResp> getUserCertificate() {
        return candidateCertificateService.getUserCertificate();
    }

    /**
     * 获取考生证书列表
     */
    @GetMapping("/getUserCertificateList/{candidateId}")
    public List<CandidateCertificateResp> getUserCertificateList(@PathVariable String candidateId) {

        return candidateCertificateService.getUserCertificateList(candidateId);
    }

    /**
     * 提交复审申请
     */

    @PostMapping("/reexamine")
    public Result submitReexamination(@RequestBody ReexaminationDTO request) {
        // 处理复证件复审申请

        return candidateCertificateService.submitReexamination(request);

    }

}