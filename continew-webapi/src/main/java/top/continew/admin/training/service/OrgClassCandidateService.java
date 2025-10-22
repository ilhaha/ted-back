package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgClassCandidateQuery;
import top.continew.admin.training.model.req.OrgClassCandidateReq;
import top.continew.admin.training.model.resp.OrgClassCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgClassCandidateResp;

/**
 * 机构班级与考生关联表业务接口
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
public interface OrgClassCandidateService extends BaseService<OrgClassCandidateResp, OrgClassCandidateDetailResp, OrgClassCandidateQuery, OrgClassCandidateReq> {}