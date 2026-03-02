package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.CandidateTypeDisableProjectQuery;
import top.continew.admin.training.model.req.CandidateTypeDisableProjectReq;
import top.continew.admin.training.model.resp.CandidateTypeDisableProjectDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeDisableProjectResp;

/**
 * 考生类型与禁考项目关联业务接口
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
public interface CandidateTypeDisableProjectService extends BaseService<CandidateTypeDisableProjectResp, CandidateTypeDisableProjectDetailResp, CandidateTypeDisableProjectQuery, CandidateTypeDisableProjectReq> {}