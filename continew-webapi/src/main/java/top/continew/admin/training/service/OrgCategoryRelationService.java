package top.continew.admin.training.service;

import top.continew.admin.training.model.query.OrgCategoryRelationQuery;
import top.continew.admin.training.model.req.OrgCategoryRelationReq;
import top.continew.admin.training.model.resp.OrgCategoryRelationDetailResp;
import top.continew.admin.training.model.resp.OrgCategoryRelationResp;
import top.continew.starter.extension.crud.service.BaseService;

/**
 * 机构与八大类关联，记录多对多关系业务接口
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
public interface OrgCategoryRelationService extends BaseService<OrgCategoryRelationResp, OrgCategoryRelationDetailResp, OrgCategoryRelationQuery, OrgCategoryRelationReq> {}