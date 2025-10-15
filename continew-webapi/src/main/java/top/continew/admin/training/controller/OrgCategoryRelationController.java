package top.continew.admin.training.controller;

import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgCategoryRelationQuery;
import top.continew.admin.training.model.req.OrgCategoryRelationReq;
import top.continew.admin.training.model.resp.OrgCategoryRelationDetailResp;
import top.continew.admin.training.model.resp.OrgCategoryRelationResp;
import top.continew.admin.training.service.OrgCategoryRelationService;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;

/**
 * 机构与八大类关联，记录多对多关系管理 API
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Tag(name = "机构与八大类关联，记录多对多关系管理 API")
@RestController
@CrudRequestMapping(value = "/admin/orgCategoryRelation", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class OrgCategoryRelationController extends BaseController<OrgCategoryRelationService, OrgCategoryRelationResp, OrgCategoryRelationDetailResp, OrgCategoryRelationQuery, OrgCategoryRelationReq> {}