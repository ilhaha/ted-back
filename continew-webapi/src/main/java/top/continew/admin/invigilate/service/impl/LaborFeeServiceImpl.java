package top.continew.admin.invigilate.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.invigilate.mapper.LaborFeeMapper;
import top.continew.admin.invigilate.model.entity.LaborFeeDO;
import top.continew.admin.invigilate.model.query.LaborFeeQuery;
import top.continew.admin.invigilate.model.req.LaborFeeReq;
import top.continew.admin.invigilate.model.resp.LaborFeeDetailResp;
import top.continew.admin.invigilate.model.resp.LaborFeeResp;
import top.continew.admin.invigilate.service.LaborFeeService;

/**
 * 考试劳务费配置业务实现
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Service
@RequiredArgsConstructor
public class LaborFeeServiceImpl extends BaseServiceImpl<LaborFeeMapper, LaborFeeDO, LaborFeeResp, LaborFeeDetailResp, LaborFeeQuery, LaborFeeReq> implements LaborFeeService {}