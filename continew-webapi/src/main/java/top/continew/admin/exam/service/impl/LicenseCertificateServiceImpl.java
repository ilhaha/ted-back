package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.entity.LicenseCertificateDO;
import top.continew.admin.exam.model.query.LicenseCertificateQuery;
import top.continew.admin.exam.model.req.LicenseCertificateReq;
import top.continew.admin.exam.model.resp.LicenseCertificateDetailResp;
import top.continew.admin.exam.model.resp.LicenseCertificateResp;
import top.continew.admin.exam.service.LicenseCertificateService;

/**
 * 人员及许可证书信息业务实现
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Service
@RequiredArgsConstructor
public class LicenseCertificateServiceImpl extends BaseServiceImpl<LicenseCertificateMapper, LicenseCertificateDO, LicenseCertificateResp, LicenseCertificateDetailResp, LicenseCertificateQuery, LicenseCertificateReq> implements LicenseCertificateService {}