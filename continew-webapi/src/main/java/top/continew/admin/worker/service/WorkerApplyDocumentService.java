package top.continew.admin.worker.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.worker.model.query.WorkerApplyDocumentQuery;
import top.continew.admin.worker.model.req.WorkerApplyDocumentReq;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentResp;

/**
 * 作业人员报名上传的资料业务接口
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
public interface WorkerApplyDocumentService extends BaseService<WorkerApplyDocumentResp, WorkerApplyDocumentDetailResp, WorkerApplyDocumentQuery, WorkerApplyDocumentReq> {}