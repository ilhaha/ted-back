package top.continew.admin.worker.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;
import top.continew.admin.worker.model.entity.WorkerExamTicketDO;
import top.continew.admin.worker.model.query.WorkerExamTicketQuery;
import top.continew.admin.worker.model.req.WorkerExamTicketReq;
import top.continew.admin.worker.model.resp.WorkerExamTicketDetailResp;
import top.continew.admin.worker.model.resp.WorkerExamTicketResp;
import top.continew.admin.worker.service.WorkerExamTicketService;

/**
 * 作业人员准考证业务实现
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Service
@RequiredArgsConstructor
public class WorkerExamTicketServiceImpl extends BaseServiceImpl<WorkerExamTicketMapper, WorkerExamTicketDO, WorkerExamTicketResp, WorkerExamTicketDetailResp, WorkerExamTicketQuery, WorkerExamTicketReq> implements WorkerExamTicketService {}