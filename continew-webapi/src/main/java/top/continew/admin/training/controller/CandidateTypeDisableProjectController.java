package top.continew.admin.training.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.CandidateTypeDisableProjectQuery;
import top.continew.admin.training.model.req.CandidateTypeDisableProjectReq;
import top.continew.admin.training.model.resp.CandidateTypeDisableProjectDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeDisableProjectResp;
import top.continew.admin.training.service.CandidateTypeDisableProjectService;

/**
 * 考生类型与禁考项目关联管理 API
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Tag(name = "考生类型与禁考项目关联管理 API")
@RestController
@CrudRequestMapping(value = "/training/candidateTypeDisableProject", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class CandidateTypeDisableProjectController extends BaseController<CandidateTypeDisableProjectService, CandidateTypeDisableProjectResp, CandidateTypeDisableProjectDetailResp, CandidateTypeDisableProjectQuery, CandidateTypeDisableProjectReq> {}