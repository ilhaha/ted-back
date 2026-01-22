package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.WeldingOperScoreQuery;
import top.continew.admin.exam.model.req.WeldingOperScoreReq;
import top.continew.admin.exam.model.resp.WeldingOperScoreDetailResp;
import top.continew.admin.exam.model.resp.WeldingOperScoreResp;

/**
 * 焊接项目实操成绩业务接口
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
public interface WeldingOperScoreService extends BaseService<WeldingOperScoreResp, WeldingOperScoreDetailResp, WeldingOperScoreQuery, WeldingOperScoreReq> {}