package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamViolationMapper;
import top.continew.admin.exam.model.entity.ExamViolationDO;
import top.continew.admin.exam.model.query.ExamViolationQuery;
import top.continew.admin.exam.model.req.ExamViolationReq;
import top.continew.admin.exam.model.resp.ExamViolationDetailResp;
import top.continew.admin.exam.model.resp.ExamViolationResp;
import top.continew.admin.exam.service.ExamViolationService;

/**
 * 考试劳务费配置业务实现
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Service
@RequiredArgsConstructor
public class ExamViolationServiceImpl extends BaseServiceImpl<ExamViolationMapper, ExamViolationDO, ExamViolationResp, ExamViolationDetailResp, ExamViolationQuery, ExamViolationReq> implements ExamViolationService {}