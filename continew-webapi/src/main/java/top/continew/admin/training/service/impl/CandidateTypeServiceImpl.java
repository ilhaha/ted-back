package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.admin.training.model.query.CandidateTypeQuery;
import top.continew.admin.training.model.req.CandidateTypeReq;
import top.continew.admin.training.model.resp.CandidateTypeDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeResp;
import top.continew.admin.training.service.CandidateTypeService;

/**
 * 考生类型业务实现
 *
 * @author ilhaha
 * @since 2025/11/03 17:57
 */
@Service
@RequiredArgsConstructor
public class CandidateTypeServiceImpl extends BaseServiceImpl<CandidateTypeMapper, CandidateTypeDO, CandidateTypeResp, CandidateTypeDetailResp, CandidateTypeQuery, CandidateTypeReq> implements CandidateTypeService {}