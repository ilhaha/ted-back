package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamAsyncErrorLogQuery;
import top.continew.admin.exam.model.req.ExamAsyncErrorLogReq;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogDetailResp;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogResp;
import top.continew.admin.exam.service.ExamAsyncErrorLogService;

/**
 * 考试异步任务错误日志管理 API
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
@Tag(name = "考试异步任务错误日志管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examAsyncErrorLog", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamAsyncErrorLogController extends BaseController<ExamAsyncErrorLogService, ExamAsyncErrorLogResp, ExamAsyncErrorLogDetailResp, ExamAsyncErrorLogQuery, ExamAsyncErrorLogReq> {}