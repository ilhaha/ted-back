package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyRecordQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyRecordReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyRecordDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyRecordResp;

/**
 * 考生报考通知对应项目-计划明细业务接口
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
public interface ExamineeNoticeApplyRecordService extends BaseService<ExamineeNoticeApplyRecordResp, ExamineeNoticeApplyRecordDetailResp, ExamineeNoticeApplyRecordQuery, ExamineeNoticeApplyRecordReq> {}