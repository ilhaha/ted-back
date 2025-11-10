package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgTrainingPriceQuery;
import top.continew.admin.training.model.req.OrgTrainingPriceReq;
import top.continew.admin.training.model.resp.OrgTrainingPriceDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPriceResp;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）业务接口
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
public interface OrgTrainingPriceService extends BaseService<OrgTrainingPriceResp, OrgTrainingPriceDetailResp, OrgTrainingPriceQuery, OrgTrainingPriceReq> {}