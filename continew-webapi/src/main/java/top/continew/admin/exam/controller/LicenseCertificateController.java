package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.LicenseCertificateQuery;
import top.continew.admin.exam.model.req.LicenseCertificateReq;
import top.continew.admin.exam.model.resp.LicenseCertificateDetailResp;
import top.continew.admin.exam.model.resp.LicenseCertificateResp;
import top.continew.admin.exam.service.LicenseCertificateService;

/**
 * 人员及许可证书信息管理 API
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Tag(name = "人员及许可证书信息管理 API")
@RestController
@CrudRequestMapping(value = "/exam/licenseCertificate", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class LicenseCertificateController extends BaseController<LicenseCertificateService, LicenseCertificateResp, LicenseCertificateDetailResp, LicenseCertificateQuery, LicenseCertificateReq> {}