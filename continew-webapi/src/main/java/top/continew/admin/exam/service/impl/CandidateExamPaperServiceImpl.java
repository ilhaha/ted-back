package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.CandidateExamPaperMapper;
import top.continew.admin.exam.model.entity.CandidateExamPaperDO;
import top.continew.admin.exam.model.query.CandidateExamPaperQuery;
import top.continew.admin.exam.model.req.CandidateExamPaperReq;
import top.continew.admin.exam.model.resp.CandidateExamPaperDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamPaperResp;
import top.continew.admin.exam.service.CandidateExamPaperService;

/**
 * 考生试卷业务实现
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
@Service
@RequiredArgsConstructor
public class CandidateExamPaperServiceImpl extends BaseServiceImpl<CandidateExamPaperMapper, CandidateExamPaperDO, CandidateExamPaperResp, CandidateExamPaperDetailResp, CandidateExamPaperQuery, CandidateExamPaperReq> implements CandidateExamPaperService {}