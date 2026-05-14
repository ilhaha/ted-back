package top.continew.admin.exam.service;

import top.continew.admin.exam.model.query.ExamNoticeQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyAuditReq;
import top.continew.admin.exam.model.resp.CandidateApplyDetailResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
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
public interface ExamineeNoticeApplyService extends BaseService<ExamineeNoticeApplyResp, ExamineeNoticeApplyDetailResp, ExamineeNoticeApplyQuery, ExamineeNoticeApplyReq> {

    /**
     * 获取通知对应的考生报名列表
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<ExamineeNoticeApplyResp> getNoticeApplyCandidatePage(ExamineeNoticeApplyQuery query, PageQuery pageQuery);

    /**
     * 获取考生报考详情
     * @param applyId
     * @return
     */
    CandidateApplyDetailResp getCandidateApplyDetail(Integer applyId);

    /**
     * 审核
     * @param applyAuditReq
     * @return
     */
    Boolean audit(ExamineeNoticeApplyAuditReq applyAuditReq);

}