package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.TrainingCheckinMapper;
import top.continew.admin.training.model.entity.TrainingCheckinDO;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;
import top.continew.admin.training.service.TrainingCheckinService;

/**
 * 培训签到记录业务实现
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Service
@RequiredArgsConstructor
public class TrainingCheckinServiceImpl extends BaseServiceImpl<TrainingCheckinMapper, TrainingCheckinDO, TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> implements TrainingCheckinService {}