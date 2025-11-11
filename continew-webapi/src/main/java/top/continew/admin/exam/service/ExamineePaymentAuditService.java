package top.continew.admin.exam.service;

import top.continew.admin.document.model.req.DocumentAuditReq;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;

import java.util.List;

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
    byte[] generatePaymentNotice(Long examPlanId, Long examineeId, String examPlanName,
                                 String projectName, Long paymentAmount, String noticeNo,
                                 String className,byte[] photoBytes) throws Exception;


    /**
     * 根据考试计划ID和考生ID查询缴费审核信息
     *
     * @param examPlanId 考试计划ID
     * @param examineeId 考生ID
     * @return 缴费审核信息
     */
    ExamineePaymentAuditDO getByExamPlanIdAndExamineeId(Long examPlanId, Long examineeId);

    /**
     * 上传缴费凭证
     */
    Boolean uploadPaymentProof(ExamineePaymentAuditResp examineePaymentAuditResp);

    /**
     * 缴费审核资料
     *
     * @param examineePaymentAuditResp 审核请求对象
     * @return true=审核成功，false=失败
     */

    boolean reviewPayment(ExamineePaymentAuditResp examineePaymentAuditResp);

    /**
     * 生成作业人员缴费通知单
     * @param enrollDOList
     */
    void generatePaymentAudit(List<EnrollDO> enrollDOList);
}