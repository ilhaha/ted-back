package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyResp;

/**
 * 考生资料关系业务接口
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
public interface ExamineeNoticeApplyService extends BaseService<ExamineeNoticeApplyResp, ExamineeNoticeApplyDetailResp, ExamineeNoticeApplyQuery, ExamineeNoticeApplyReq> {}