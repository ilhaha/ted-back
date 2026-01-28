package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.CandidateExamProjectQuery;
import top.continew.admin.exam.model.req.CandidateExamProjectReq;
import top.continew.admin.exam.model.resp.CandidateExamProjectDetailResp;
import top.continew.admin.exam.model.resp.CandidateExamProjectResp;

/**
 * 考生-考试项目考试状态业务接口
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
public interface CandidateExamProjectService extends BaseService<CandidateExamProjectResp, CandidateExamProjectDetailResp, CandidateExamProjectQuery, CandidateExamProjectReq> {}