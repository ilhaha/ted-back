package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgCandidateQuery;
import top.continew.admin.training.model.req.OrgCandidateReq;
import top.continew.admin.training.model.resp.OrgCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgCandidateResp;

/**
 * 机构考生关联业务接口
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
public interface OrgCandidateService extends BaseService<OrgCandidateResp, OrgCandidateDetailResp, OrgCandidateQuery, OrgCandidateReq> {

    /**
     * 机构审核考生加入机构
     * @param orgCandidateReq
     * @return
     */
    Boolean review(OrgCandidateReq orgCandidateReq);
}