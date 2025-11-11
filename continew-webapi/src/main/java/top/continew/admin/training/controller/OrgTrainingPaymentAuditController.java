package top.continew.admin.training.controller;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
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

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理 API
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Tag(name = "机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理 API")
@RestController
@CrudRequestMapping(value = "/training/orgTrainingPaymentAudit", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class OrgTrainingPaymentAuditController extends BaseController<OrgTrainingPaymentAuditService, OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> {

    /**
     * 根据机构id和是申请加入机构审核id查询培训缴费审核信息
     */
    @GetMapping("/info")
    public OrgTrainingPaymentAuditDO getTrainingPaymentAuditInfo(
            @RequestParam Long orgId,
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

}