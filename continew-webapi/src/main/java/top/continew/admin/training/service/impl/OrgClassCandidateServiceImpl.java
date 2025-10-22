package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.query.OrgClassCandidateQuery;
import top.continew.admin.training.model.req.OrgClassCandidateReq;
import top.continew.admin.training.model.resp.OrgClassCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgClassCandidateResp;
import top.continew.admin.training.service.OrgClassCandidateService;

/**
 * 机构班级与考生关联表业务实现
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Service
@RequiredArgsConstructor
public class OrgClassCandidateServiceImpl extends BaseServiceImpl<OrgClassCandidateMapper, OrgClassCandidateDO, OrgClassCandidateResp, OrgClassCandidateDetailResp, OrgClassCandidateQuery, OrgClassCandidateReq> implements OrgClassCandidateService {}