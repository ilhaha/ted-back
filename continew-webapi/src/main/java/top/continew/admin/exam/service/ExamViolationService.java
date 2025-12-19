package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamViolationQuery;
import top.continew.admin.exam.model.req.ExamViolationReq;
import top.continew.admin.exam.model.resp.ExamViolationDetailResp;
import top.continew.admin.exam.model.resp.ExamViolationResp;

/**
 * 考试劳务费配置业务接口
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
public interface ExamViolationService extends BaseService<ExamViolationResp, ExamViolationDetailResp, ExamViolationQuery, ExamViolationReq> {}