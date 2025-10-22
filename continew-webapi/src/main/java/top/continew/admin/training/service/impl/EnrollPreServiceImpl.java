package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.EnrollPreMapper;
import top.continew.admin.training.model.entity.EnrollPreDO;
import top.continew.admin.training.model.query.EnrollPreQuery;
import top.continew.admin.training.model.req.EnrollPreReq;
import top.continew.admin.training.model.resp.EnrollPreDetailResp;
import top.continew.admin.training.model.resp.EnrollPreResp;
import top.continew.admin.training.service.EnrollPreService;

/**
 * 机构考生预报名业务实现
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Service
@RequiredArgsConstructor
public class EnrollPreServiceImpl extends BaseServiceImpl<EnrollPreMapper, EnrollPreDO, EnrollPreResp, EnrollPreDetailResp, EnrollPreQuery, EnrollPreReq> implements EnrollPreService {}