package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyRecordQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyRecordReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyRecordDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyRecordResp;
import top.continew.admin.exam.service.ExamineeNoticeApplyRecordService;

/**
 * 考生报考通知对应项目-计划明细管理 API
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Tag(name = "考生报考通知对应项目-计划明细管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examineeNoticeApplyRecord", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamineeNoticeApplyRecordController extends BaseController<ExamineeNoticeApplyRecordService, ExamineeNoticeApplyRecordResp, ExamineeNoticeApplyRecordDetailResp, ExamineeNoticeApplyRecordQuery, ExamineeNoticeApplyRecordReq> {}