package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.WeldingOperScoreMapper;
import top.continew.admin.exam.model.entity.WeldingOperScoreDO;
import top.continew.admin.exam.model.query.WeldingOperScoreQuery;
import top.continew.admin.exam.model.req.WeldingOperScoreReq;
import top.continew.admin.exam.model.resp.WeldingOperScoreDetailResp;
import top.continew.admin.exam.model.resp.WeldingOperScoreResp;
import top.continew.admin.exam.service.WeldingOperScoreService;

/**
 * 焊接项目实操成绩业务实现
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Service
@RequiredArgsConstructor
public class WeldingOperScoreServiceImpl extends BaseServiceImpl<WeldingOperScoreMapper, WeldingOperScoreDO, WeldingOperScoreResp, WeldingOperScoreDetailResp, WeldingOperScoreQuery, WeldingOperScoreReq> implements WeldingOperScoreService {}