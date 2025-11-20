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

package top.continew.admin.training.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgTrainingPaymentAuditQuery;
import top.continew.admin.training.model.req.OrgTrainingPaymentAuditReq;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditResp;
import top.continew.admin.training.service.OrgTrainingPaymentAuditService;
import top.continew.starter.web.model.R;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理 API
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Tag(name = "机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理 API")
@RestController
@CrudRequestMapping(value = "/training/orgTrainingPaymentAudit", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class OrgTrainingPaymentAuditController extends BaseController<OrgTrainingPaymentAuditService, OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> {

    /**
     * 根据机构id和是申请加入机构审核id查询培训缴费审核信息
     */
    @GetMapping("/info")
    public OrgTrainingPaymentAuditDO getTrainingPaymentAuditInfo(@RequestParam Long orgId,
                                                                 @RequestParam Long enrollId) {
        return baseService.getByTrainingOrgIdAndEnrollId(orgId, enrollId);
    }

    /**
     * 上传培训缴费凭证
     */
    @PostMapping("/uploadProof")
    public Boolean uploadTrainingPaymentProof(@Validated @RequestBody OrgTrainingPaymentAuditResp orgTrainingPaymentAuditResp) {
        return baseService.uploadTrainingPaymentProof(orgTrainingPaymentAuditResp);
    }

    /**
     * 审核培训缴费记录
     */
    @PostMapping("/reviewTrainingPayment")
    @Operation(summary = "缴费审核接口")
    public R<Boolean> reviewTrainingPayment(@RequestBody @Validated OrgTrainingPaymentAuditResp orgTrainingPaymentAuditResp) {
        boolean result = baseService.reviewTrainingPayment(orgTrainingPaymentAuditResp);
        return R.ok(result);
    }

    /**
     * 培训缴费退费
     */
    @PostMapping("/refundTrainingPayment")
    public Boolean refundTrainingPayment(@RequestParam Long id) {
        return baseService.refundTrainingPayment(id);
    }

}