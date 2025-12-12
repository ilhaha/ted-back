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

package top.continew.admin.exam.service;

import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.req.PaymentAuditConfirmReq;
import top.continew.admin.exam.model.req.PaymentInfoReq;
import top.continew.admin.exam.model.req.ReviewPaymentReq;
import top.continew.admin.exam.model.resp.PaymentInfoVO;
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
     * 
     * @param examineeId
     * @return
     */
    Boolean verifyPaymentAudit(Long examineeId);

    /**
     * 报名审核通过生成缴费审核表
     *
     */
    void generatePaymentAudit(Long examPlanId, Long examineeId, Long enrollId) throws Exception;

    /**
     * 生成缴费通知单
     */
    byte[] generatePaymentNotice(Long examPlanId,
                                 Long examineeId,
                                 String examPlanName,
                                 String projectName,
                                 Long paymentAmount,
                                 String noticeNo,
                                 String className,
                                 byte[] photoBytes) throws Exception;

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
     * @param reviewPaymentReq 审核请求对象
     * @return true=审核成功，false=失败
     */
    boolean reviewPayment(ReviewPaymentReq reviewPaymentReq);

    /**
     * 生成作业人员缴费通知单
     * 
     * @param enrollDOList
     */
    void generatePaymentAudit(List<EnrollDO> enrollDOList);

    /**
     * 扫码查询作业人员缴费信息
     *
     * @param paymentInfoReq
     * @return
     */
    PaymentInfoVO getPaymentInfoByQrcode(PaymentInfoReq paymentInfoReq);

    /**
     * 扫码确认提交作业人员缴费信息
     *
     * @param paymentAuditConfirmReq
     * @return
     */
    Boolean paymentAuditConfirm(PaymentAuditConfirmReq paymentAuditConfirmReq);
}