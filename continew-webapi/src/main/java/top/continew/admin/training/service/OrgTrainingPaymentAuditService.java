package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgTrainingPaymentAuditQuery;
import top.continew.admin.training.model.req.OrgTrainingPaymentAuditReq;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditResp;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）业务接口
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
public interface OrgTrainingPaymentAuditService extends BaseService<OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> {}