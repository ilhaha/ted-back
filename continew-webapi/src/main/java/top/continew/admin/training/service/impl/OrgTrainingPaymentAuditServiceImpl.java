package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgTrainingPaymentAuditMapper;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;
import top.continew.admin.training.model.query.OrgTrainingPaymentAuditQuery;
import top.continew.admin.training.model.req.OrgTrainingPaymentAuditReq;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditResp;
import top.continew.admin.training.service.OrgTrainingPaymentAuditService;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）业务实现
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Service
@RequiredArgsConstructor
public class OrgTrainingPaymentAuditServiceImpl extends BaseServiceImpl<OrgTrainingPaymentAuditMapper, OrgTrainingPaymentAuditDO, OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> implements OrgTrainingPaymentAuditService {}