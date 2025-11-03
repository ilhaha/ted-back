package top.continew.admin.worker.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;
import top.continew.admin.worker.model.query.WorkerApplyDocumentQuery;
import top.continew.admin.worker.model.req.WorkerApplyDocumentReq;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentResp;
import top.continew.admin.worker.service.WorkerApplyDocumentService;

/**
 * 作业人员报名上传的资料业务实现
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Service
@RequiredArgsConstructor
public class WorkerApplyDocumentServiceImpl extends BaseServiceImpl<WorkerApplyDocumentMapper, WorkerApplyDocumentDO, WorkerApplyDocumentResp, WorkerApplyDocumentDetailResp, WorkerApplyDocumentQuery, WorkerApplyDocumentReq> implements WorkerApplyDocumentService {}