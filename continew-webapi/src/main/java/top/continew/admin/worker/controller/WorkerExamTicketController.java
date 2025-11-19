package top.continew.admin.worker.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.worker.model.query.WorkerExamTicketQuery;
import top.continew.admin.worker.model.req.WorkerExamTicketReq;
import top.continew.admin.worker.model.resp.WorkerExamTicketDetailResp;
import top.continew.admin.worker.model.resp.WorkerExamTicketResp;
import top.continew.admin.worker.service.WorkerExamTicketService;

/**
 * 作业人员准考证管理 API
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Tag(name = "作业人员准考证管理 API")
@RestController
@CrudRequestMapping(value = "/worker/workerExamTicket", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class WorkerExamTicketController extends BaseController<WorkerExamTicketService, WorkerExamTicketResp, WorkerExamTicketDetailResp, WorkerExamTicketQuery, WorkerExamTicketReq> {}