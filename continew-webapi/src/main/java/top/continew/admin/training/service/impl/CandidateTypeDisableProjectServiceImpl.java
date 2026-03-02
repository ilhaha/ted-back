package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.CandidateTypeDisableProjectMapper;
import top.continew.admin.training.model.entity.CandidateTypeDisableProjectDO;
import top.continew.admin.training.model.query.CandidateTypeDisableProjectQuery;
import top.continew.admin.training.model.req.CandidateTypeDisableProjectReq;
import top.continew.admin.training.model.resp.CandidateTypeDisableProjectDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeDisableProjectResp;
import top.continew.admin.training.service.CandidateTypeDisableProjectService;

/**
 * 考生类型与禁考项目关联业务实现
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Service
@RequiredArgsConstructor
public class CandidateTypeDisableProjectServiceImpl extends BaseServiceImpl<CandidateTypeDisableProjectMapper, CandidateTypeDisableProjectDO, CandidateTypeDisableProjectResp, CandidateTypeDisableProjectDetailResp, CandidateTypeDisableProjectQuery, CandidateTypeDisableProjectReq> implements CandidateTypeDisableProjectService {}