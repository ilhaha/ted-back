package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.LicenseCertificateQuery;
import top.continew.admin.exam.model.req.LicenseCertificateReq;
import top.continew.admin.exam.model.resp.LicenseCertificateDetailResp;
import top.continew.admin.exam.model.resp.LicenseCertificateResp;

/**
 * 人员及许可证书信息业务接口
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
public interface LicenseCertificateService extends BaseService<LicenseCertificateResp, LicenseCertificateDetailResp, LicenseCertificateQuery, LicenseCertificateReq> {}