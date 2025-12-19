package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamViolationQuery;
import top.continew.admin.exam.model.req.ExamViolationReq;
import top.continew.admin.exam.model.resp.ExamViolationDetailResp;
import top.continew.admin.exam.model.resp.ExamViolationResp;
import top.continew.admin.exam.service.ExamViolationService;

/**
 * 考试劳务费配置管理 API
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Tag(name = "考试劳务费配置管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examViolation", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamViolationController extends BaseController<ExamViolationService, ExamViolationResp, ExamViolationDetailResp, ExamViolationQuery, ExamViolationReq> {}