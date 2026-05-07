package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamineeNoticeApplyRecordMapper;
import top.continew.admin.exam.model.entity.ExamineeNoticeApplyRecordDO;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyRecordQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyRecordReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyRecordDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyRecordResp;
import top.continew.admin.exam.service.ExamineeNoticeApplyRecordService;

/**
 * 考生报考通知对应项目-计划明细业务实现
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Service
@RequiredArgsConstructor
public class ExamineeNoticeApplyRecordServiceImpl extends BaseServiceImpl<ExamineeNoticeApplyRecordMapper, ExamineeNoticeApplyRecordDO, ExamineeNoticeApplyRecordResp, ExamineeNoticeApplyRecordDetailResp, ExamineeNoticeApplyRecordQuery, ExamineeNoticeApplyRecordReq> implements ExamineeNoticeApplyRecordService {}