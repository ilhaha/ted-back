package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.EnrollPreQuery;
import top.continew.admin.training.model.req.EnrollPreReq;
import top.continew.admin.training.model.resp.EnrollPreDetailResp;
import top.continew.admin.training.model.resp.EnrollPreResp;

/**
 * 机构考生预报名业务接口
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
public interface EnrollPreService extends BaseService<EnrollPreResp, EnrollPreDetailResp, EnrollPreQuery, EnrollPreReq> {}