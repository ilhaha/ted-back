package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.PersonQualificationMapper;
import top.continew.admin.exam.model.entity.PersonQualificationDO;
import top.continew.admin.exam.model.query.PersonQualificationQuery;
import top.continew.admin.exam.model.req.PersonQualificationReq;
import top.continew.admin.exam.model.resp.PersonQualificationDetailResp;
import top.continew.admin.exam.model.resp.PersonQualificationResp;
import top.continew.admin.exam.service.PersonQualificationService;

/**
 * 人员复审信息表业务实现
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Service
@RequiredArgsConstructor
public class PersonQualificationServiceImpl extends BaseServiceImpl<PersonQualificationMapper, PersonQualificationDO, PersonQualificationResp, PersonQualificationDetailResp, PersonQualificationQuery, PersonQualificationReq> implements PersonQualificationService {}