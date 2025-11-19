package top.continew.admin.worker.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.worker.model.query.WorkerExamTicketQuery;
import top.continew.admin.worker.model.req.WorkerExamTicketReq;
import top.continew.admin.worker.model.resp.WorkerExamTicketDetailResp;
import top.continew.admin.worker.model.resp.WorkerExamTicketResp;

/**
 * 作业人员准考证业务接口
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
public interface WorkerExamTicketService extends BaseService<WorkerExamTicketResp, WorkerExamTicketDetailResp, WorkerExamTicketQuery, WorkerExamTicketReq> {}