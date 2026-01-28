package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.PlanApplyClassQuery;
import top.continew.admin.exam.model.req.PlanApplyClassReq;
import top.continew.admin.exam.model.resp.PlanApplyClassDetailResp;
import top.continew.admin.exam.model.resp.PlanApplyClassResp;

/**
 * 考试计划报考班级业务接口
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
public interface PlanApplyClassService extends BaseService<PlanApplyClassResp, PlanApplyClassDetailResp, PlanApplyClassQuery, PlanApplyClassReq> {}