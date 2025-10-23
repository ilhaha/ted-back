package top.continew.admin.document.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.document.model.query.DocumentPreQuery;
import top.continew.admin.document.model.req.DocumentPreReq;
import top.continew.admin.document.model.resp.DocumentPreDetailResp;
import top.continew.admin.document.model.resp.DocumentPreResp;
import top.continew.admin.document.service.DocumentPreService;

/**
 * 机构报考-考生上传资料管理 API
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Tag(name = "机构报考-考生上传资料管理 API")
@RestController
@CrudRequestMapping(value = "/document/documentPre", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class DocumentPreController extends BaseController<DocumentPreService, DocumentPreResp, DocumentPreDetailResp, DocumentPreQuery, DocumentPreReq> {}