package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.CandidateExamPaperQuery;
import top.continew.admin.exam.model.req.CandidateExamPaperReq;
import top.continew.admin.exam.model.resp.CandidateExamPaperDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamPaperResp;

/**
 * 考生试卷业务接口
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
public interface CandidateExamPaperService extends BaseService<CandidateExamPaperResp, CandidateExamPaperDetailResp, CandidateExamPaperQuery, CandidateExamPaperReq> {}