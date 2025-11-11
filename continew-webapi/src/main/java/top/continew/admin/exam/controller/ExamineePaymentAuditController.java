package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.document.model.req.DocumentAuditReq;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.req.PaymentInfoReq;
import top.continew.admin.exam.model.resp.PaymentInfoVO;
import top.continew.admin.util.Result;
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
@CrudRequestMapping(value = "/exam/examineePaymentAudit", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamineePaymentAuditController extends BaseController<ExamineePaymentAuditService, ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> {

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
    public ExamineePaymentAuditDO getPaymentAuditInfo(
            @RequestParam Long examPlanId,
            @RequestParam Long examineeId) {
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
    public R<Boolean> reviewPayment(@RequestBody @Validated ExamineePaymentAuditResp examineePaymentAuditResp) {
        boolean result = baseService.reviewPayment(examineePaymentAuditResp);
        return R.ok(result);
    }


}