package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamineePaymentAuditMapper;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.exam.service.ExamineePaymentAuditService;

/**
 * 考生缴费审核业务实现
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Service
@RequiredArgsConstructor
public class ExamineePaymentAuditServiceImpl extends BaseServiceImpl<ExamineePaymentAuditMapper, ExamineePaymentAuditDO, ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> implements ExamineePaymentAuditService {}