package top.continew.admin.training.service;

import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgTrainingPaymentAuditQuery;
import top.continew.admin.training.model.req.OrgTrainingPaymentAuditReq;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditResp;

import java.math.BigDecimal;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）业务接口
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
public interface OrgTrainingPaymentAuditService extends BaseService<OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> {

    /**
     * 根据机构id和是申请加入机构审核id查询培训缴费审核信息
     *
     * @param orgId 机构id
     * @param enrollId 考生申请报名加入机构id
     * @return 缴费审核信息
     */
    OrgTrainingPaymentAuditDO getByTrainingOrgIdAndEnrollId(Long orgId, Long enrollId);


    byte[] generateTrainingPaymentNotice(String orgName, String projectName, String noticeNo, BigDecimal paymentAmount);


    /**
     * 上传培训缴费凭证
     */
    Boolean uploadTrainingPaymentProof(OrgTrainingPaymentAuditResp orgTrainingPaymentAuditResp);



    /**
     * 审核培训缴费资料
     *
     * @param orgTrainingPaymentAuditResp 审核请求对象
     * @return true=审核成功，false=失败
     */

    boolean reviewTrainingPayment(OrgTrainingPaymentAuditResp orgTrainingPaymentAuditResp);

    /**
     * 培训缴费退费
     * @param id 缴费记录ID
     * @return 是否退费成功
     */
    Boolean refundTrainingPayment(Long id);


}