package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamAsyncErrorLogMapper;
import top.continew.admin.exam.model.entity.ExamAsyncErrorLogDO;
import top.continew.admin.exam.model.query.ExamAsyncErrorLogQuery;
import top.continew.admin.exam.model.req.ExamAsyncErrorLogReq;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogDetailResp;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogResp;
import top.continew.admin.exam.service.ExamAsyncErrorLogService;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 考试异步任务错误日志业务实现
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
@Service
@RequiredArgsConstructor
public class ExamAsyncErrorLogServiceImpl extends BaseServiceImpl<ExamAsyncErrorLogMapper, ExamAsyncErrorLogDO, ExamAsyncErrorLogResp, ExamAsyncErrorLogDetailResp, ExamAsyncErrorLogQuery, ExamAsyncErrorLogReq> implements ExamAsyncErrorLogService {

    /**
     * 记录错误信息
     * @param planId
     * @param enrollId
     * @param step
     * @param e
     */
    @Override
    public void recordError(Long planId,
                             Long enrollId,
                             String step,
                             Exception e) {

        try {
            ExamAsyncErrorLogDO logDO = new ExamAsyncErrorLogDO();
            logDO.setPlanId(planId);
            logDO.setEnrollId(enrollId);
            logDO.setStep(step);
            logDO.setErrorMsg(e.getMessage());

            String stack = Arrays.stream(e.getStackTrace())
                    .limit(10)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n"));

            logDO.setStackTrace(stack);
            logDO.setStatus(0);

            baseMapper.insert(logDO);

        } catch (Exception ex) {
            log.error("记录错误日志失败", ex);
        }
    }
}