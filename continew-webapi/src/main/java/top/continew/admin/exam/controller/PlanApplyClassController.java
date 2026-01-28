package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.PlanApplyClassQuery;
import top.continew.admin.exam.model.req.PlanApplyClassReq;
import top.continew.admin.exam.model.resp.PlanApplyClassDetailResp;
import top.continew.admin.exam.model.resp.PlanApplyClassResp;
import top.continew.admin.exam.service.PlanApplyClassService;

/**
 * 考试计划报考班级管理 API
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Tag(name = "考试计划报考班级管理 API")
@RestController
@CrudRequestMapping(value = "/exam/planApplyClass", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class PlanApplyClassController extends BaseController<PlanApplyClassService, PlanApplyClassResp, PlanApplyClassDetailResp, PlanApplyClassQuery, PlanApplyClassReq> {}