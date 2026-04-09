package top.continew.admin.exam.service;

import top.continew.admin.exam.model.entity.ExamAsyncErrorLogDO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamAsyncErrorLogQuery;
import top.continew.admin.exam.model.req.ExamAsyncErrorLogReq;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogDetailResp;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogResp;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 考试异步任务错误日志业务接口
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
public interface ExamAsyncErrorLogService extends BaseService<ExamAsyncErrorLogResp, ExamAsyncErrorLogDetailResp, ExamAsyncErrorLogQuery, ExamAsyncErrorLogReq> {

     void recordError(Long planId, Long enrollId, String step, Exception e);
}