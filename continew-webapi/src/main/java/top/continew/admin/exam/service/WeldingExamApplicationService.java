package top.continew.admin.exam.service;

import top.continew.admin.exam.model.req.ReviewWeldingExamApplicationReq;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.WeldingExamApplicationQuery;
import top.continew.admin.exam.model.req.WeldingExamApplicationReq;
import top.continew.admin.exam.model.resp.WeldingExamApplicationDetailResp;
import top.continew.admin.exam.model.resp.WeldingExamApplicationResp;

/**
 * 机构申请焊接考试项目业务接口
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
public interface WeldingExamApplicationService extends BaseService<WeldingExamApplicationResp, WeldingExamApplicationDetailResp, WeldingExamApplicationQuery, WeldingExamApplicationReq> {
    /**
     * 审核
     * @param req
     * @return
     */
    Boolean review(ReviewWeldingExamApplicationReq req);
}