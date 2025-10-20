package top.continew.admin.training.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;
import top.continew.admin.training.service.OrgClassService;

/**
 * 培训机构班级管理 API
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Tag(name = "培训机构班级管理 API")
@RestController
@CrudRequestMapping(value = "/training/orgClass", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class OrgClassController extends BaseController<OrgClassService, OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> {}