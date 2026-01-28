package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.CandidateExamProjectQuery;
import top.continew.admin.exam.model.req.CandidateExamProjectReq;
import top.continew.admin.exam.model.resp.CandidateExamProjectDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamProjectResp;
import top.continew.admin.exam.service.CandidateExamProjectService;

/**
 * 考生-考试项目考试状态管理 API
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Tag(name = "考生-考试项目考试状态管理 API")
@RestController
@CrudRequestMapping(value = "/exam/candidateExamProject", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class CandidateExamProjectController extends BaseController<CandidateExamProjectService, CandidateExamProjectResp, CandidateExamProjectDetailResp, CandidateExamProjectQuery, CandidateExamProjectReq> {}