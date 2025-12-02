package top.continew.admin.invigilate.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.invigilate.mapper.UserQualificationMapper;
import top.continew.admin.invigilate.model.entity.UserQualificationDO;
import top.continew.admin.invigilate.model.query.UserQualificationQuery;
import top.continew.admin.invigilate.model.req.UserQualificationReq;
import top.continew.admin.invigilate.model.resp.UserQualificationDetailResp;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;
import top.continew.admin.invigilate.service.UserQualificationService;

/**
 * 监考员资质证明业务实现
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Service
@RequiredArgsConstructor
public class UserQualificationServiceImpl extends BaseServiceImpl<UserQualificationMapper, UserQualificationDO, UserQualificationResp, UserQualificationDetailResp, UserQualificationQuery, UserQualificationReq> implements UserQualificationService {}