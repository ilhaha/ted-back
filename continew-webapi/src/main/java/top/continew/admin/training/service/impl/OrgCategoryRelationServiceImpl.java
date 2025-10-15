package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.training.mapper.OrgCategoryRelationMapper;
import top.continew.admin.training.model.entity.OrgCategoryRelationDO;
import top.continew.admin.training.model.query.OrgCategoryRelationQuery;
import top.continew.admin.training.model.req.OrgCategoryRelationReq;
import top.continew.admin.training.model.resp.OrgCategoryRelationDetailResp;
import top.continew.admin.training.model.resp.OrgCategoryRelationResp;
import top.continew.admin.training.service.OrgCategoryRelationService;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

/**
 * 机构与八大类关联，记录多对多关系业务实现
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Service
@RequiredArgsConstructor
public class OrgCategoryRelationServiceImpl extends BaseServiceImpl<OrgCategoryRelationMapper, OrgCategoryRelationDO, OrgCategoryRelationResp, OrgCategoryRelationDetailResp, OrgCategoryRelationQuery, OrgCategoryRelationReq> implements OrgCategoryRelationService {}