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

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.req.PaymentAuditConfirmReq;
import top.continew.admin.exam.model.req.PaymentInfoReq;
import top.continew.admin.exam.model.req.ReviewPaymentReq;
import top.continew.admin.exam.model.resp.PaymentInfoVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.starter.web.model.R;

/**
 * 考生缴费审核管理 API
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Tag(name = "考生缴费审核管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examineePaymentAudit", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ExamineePaymentAuditController extends BaseController<ExamineePaymentAuditService, ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> {

    /**
     * 扫码确认提交作业人员缴费信息
     *
     * @param paymentAuditConfirmReq
     * @return
     */
    @SaIgnore
    @PostMapping("/payment/qrcode/confirm")
    public Boolean paymentAuditConfirm(@Validated @RequestBody PaymentAuditConfirmReq paymentAuditConfirmReq) {
        return baseService.paymentAuditConfirm(paymentAuditConfirmReq);
    }

    /**
     * 扫码查询作业人员缴费信息
     *
     * @param paymentInfoReq
     * @return
     */
    @SaIgnore
    @PostMapping("/payment/qrcode")
    public PaymentInfoVO getPaymentInfoByQrcode(@Validated @RequestBody PaymentInfoReq paymentInfoReq) {
        return baseService.getPaymentInfoByQrcode(paymentInfoReq);
    }

    /**
     * 根据考试计划ID和考生ID查询缴费审核信息
     */
    @GetMapping("/info")
    public ExamineePaymentAuditDO getPaymentAuditInfo(@RequestParam Long examPlanId, @RequestParam Long examineeId) {
        return baseService.getByExamPlanIdAndExamineeId(examPlanId, examineeId);
    }

    /**
     * 上传缴费凭证
     */
    @PostMapping("/payment/uploadProof")
    public Boolean uploadPaymentProof(@Validated @RequestBody ExamineePaymentAuditResp examineePaymentAuditResp) {
        return baseService.uploadPaymentProof(examineePaymentAuditResp);
    }

    /**
     * 审核缴费记录
     */
    @PostMapping("/reviewPayment")
    @Operation(summary = "缴费审核接口")
    public R<Boolean> reviewPayment(@RequestBody @Validated ReviewPaymentReq reviewPaymentReq) {
        boolean result = baseService.reviewPayment(reviewPaymentReq);
        return R.ok(result);
    }

}