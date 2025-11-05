package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
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
public interface ExamineePaymentAuditService extends BaseService<ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> {

    /**
     * 考生查看缴费审核表信息
     * @param examineeId
     * @return
     */
    Boolean verifyPaymentAudit(Long examineeId);


    /**
     * 报名审核通过生成缴费审核表
     *
     */
    void generatePaymentAudit(Long examPlanId, Long examineeId , Long enrollId) throws Exception;

    /**
     * 生成缴费通知单
     */
    byte[] generatePaymentNotice(Long examPlanId, Long examineeId, String examPlanName, String projectName, Long paymentAmount) throws Exception;
}