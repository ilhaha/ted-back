package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.CandidateTypeQuery;
import top.continew.admin.training.model.req.CandidateTypeReq;
import top.continew.admin.training.model.resp.CandidateTypeDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeResp;

/**
 * 考生类型业务接口
 *
 * @author ilhaha
 * @since 2025/11/03 17:57
 */
public interface CandidateTypeService extends BaseService<CandidateTypeResp, CandidateTypeDetailResp, CandidateTypeQuery, CandidateTypeReq> {}