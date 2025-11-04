package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;

/**
 * 考生缴费审核业务接口
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
public interface ExamineePaymentAuditService extends BaseService<ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> {}