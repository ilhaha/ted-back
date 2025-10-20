package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;

/**
 * 培训机构班级业务接口
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
public interface OrgClassService extends BaseService<OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> {}