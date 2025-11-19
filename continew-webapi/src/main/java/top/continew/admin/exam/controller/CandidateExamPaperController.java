package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.CandidateExamPaperQuery;
import top.continew.admin.exam.model.req.CandidateExamPaperReq;
import top.continew.admin.exam.model.resp.CandidateExamPaperDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamPaperResp;
import top.continew.admin.exam.service.CandidateExamPaperService;

/**
 * 考生试卷管理 API
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
@Tag(name = "考生试卷管理 API")
@RestController
@CrudRequestMapping(value = "/exam/candidateExamPaper", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class CandidateExamPaperController extends BaseController<CandidateExamPaperService, CandidateExamPaperResp, CandidateExamPaperDetailResp, CandidateExamPaperQuery, CandidateExamPaperReq> {}