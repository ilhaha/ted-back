package top.continew.admin.training.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgTrainingPriceMapper;
import top.continew.admin.training.model.entity.OrgTrainingPriceDO;
import top.continew.admin.training.model.query.OrgTrainingPriceQuery;
import top.continew.admin.training.model.req.OrgTrainingPriceReq;
import top.continew.admin.training.model.resp.OrgTrainingPriceDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPriceResp;
import top.continew.admin.training.service.OrgTrainingPriceService;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）业务实现
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
@Service
@RequiredArgsConstructor
public class OrgTrainingPriceServiceImpl extends BaseServiceImpl<OrgTrainingPriceMapper, OrgTrainingPriceDO, OrgTrainingPriceResp, OrgTrainingPriceDetailResp, OrgTrainingPriceQuery, OrgTrainingPriceReq> implements OrgTrainingPriceService {}