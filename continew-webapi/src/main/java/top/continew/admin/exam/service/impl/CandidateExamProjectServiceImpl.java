package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.CandidateExamProjectMapper;
import top.continew.admin.exam.model.entity.CandidateExamProjectDO;
import top.continew.admin.exam.model.query.CandidateExamProjectQuery;
import top.continew.admin.exam.model.req.CandidateExamProjectReq;
import top.continew.admin.exam.model.resp.CandidateExamProjectDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamProjectResp;
import top.continew.admin.exam.service.CandidateExamProjectService;

/**
 * 考生-考试项目考试状态业务实现
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Service
@RequiredArgsConstructor
public class CandidateExamProjectServiceImpl extends BaseServiceImpl<CandidateExamProjectMapper, CandidateExamProjectDO, CandidateExamProjectResp, CandidateExamProjectDetailResp, CandidateExamProjectQuery, CandidateExamProjectReq> implements CandidateExamProjectService {}