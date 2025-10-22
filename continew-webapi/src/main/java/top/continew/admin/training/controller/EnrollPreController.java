package top.continew.admin.training.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.EnrollPreQuery;
import top.continew.admin.training.model.req.EnrollPreReq;
import top.continew.admin.training.model.resp.EnrollPreDetailResp;
import top.continew.admin.training.model.resp.EnrollPreResp;
import top.continew.admin.training.service.EnrollPreService;

/**
 * 机构考生预报名管理 API
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Tag(name = "机构考生预报名管理 API")
@RestController
@CrudRequestMapping(value = "/training/enrollPre", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class EnrollPreController extends BaseController<EnrollPreService, EnrollPreResp, EnrollPreDetailResp, EnrollPreQuery, EnrollPreReq> {


}