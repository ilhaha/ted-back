package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamineeNoticeApplyMapper;
import top.continew.admin.exam.model.entity.ExamineeNoticeApplyDO;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyResp;
import top.continew.admin.exam.service.ExamineeNoticeApplyService;

/**
 * 考生资料关系业务实现
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Service
@RequiredArgsConstructor
public class ExamineeNoticeApplyServiceImpl extends BaseServiceImpl<ExamineeNoticeApplyMapper, ExamineeNoticeApplyDO, ExamineeNoticeApplyResp, ExamineeNoticeApplyDetailResp, ExamineeNoticeApplyQuery, ExamineeNoticeApplyReq> implements ExamineeNoticeApplyService {}