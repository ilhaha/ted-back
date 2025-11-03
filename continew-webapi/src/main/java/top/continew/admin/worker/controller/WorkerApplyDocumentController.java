package top.continew.admin.worker.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.worker.model.query.WorkerApplyDocumentQuery;
import top.continew.admin.worker.model.req.WorkerApplyDocumentReq;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentResp;
import top.continew.admin.worker.service.WorkerApplyDocumentService;

/**
 * 作业人员报名上传的资料管理 API
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Tag(name = "作业人员报名上传的资料管理 API")
@RestController
@CrudRequestMapping(value = "/worker/workerApplyDocument", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class WorkerApplyDocumentController extends BaseController<WorkerApplyDocumentService, WorkerApplyDocumentResp, WorkerApplyDocumentDetailResp, WorkerApplyDocumentQuery, WorkerApplyDocumentReq> {}