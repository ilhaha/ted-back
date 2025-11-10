package top.continew.admin.training.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgTrainingPriceQuery;
import top.continew.admin.training.model.req.OrgTrainingPriceReq;
import top.continew.admin.training.model.resp.OrgTrainingPriceDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPriceResp;
import top.continew.admin.training.service.OrgTrainingPriceService;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）管理 API
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
@Tag(name = "机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）管理 API")
@RestController
@CrudRequestMapping(value = "/training/orgTrainingPrice", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class OrgTrainingPriceController extends BaseController<OrgTrainingPriceService, OrgTrainingPriceResp, OrgTrainingPriceDetailResp, OrgTrainingPriceQuery, OrgTrainingPriceReq> {}