package top.continew.admin.training.controller;

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
public class OrgTrainingPaymentAuditController extends BaseController<OrgTrainingPaymentAuditService, OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> {}