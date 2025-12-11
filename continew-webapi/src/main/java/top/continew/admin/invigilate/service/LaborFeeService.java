package top.continew.admin.invigilate.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.invigilate.model.query.LaborFeeQuery;
import top.continew.admin.invigilate.model.req.LaborFeeReq;
import top.continew.admin.invigilate.model.resp.LaborFeeDetailResp;
import top.continew.admin.invigilate.model.resp.LaborFeeResp;

/**
 * 考试劳务费配置业务接口
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
public interface LaborFeeService extends BaseService<LaborFeeResp, LaborFeeDetailResp, LaborFeeQuery, LaborFeeReq> {}