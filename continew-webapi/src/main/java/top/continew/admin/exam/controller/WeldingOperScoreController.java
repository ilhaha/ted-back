package top.continew.admin.exam.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.WeldingOperScoreQuery;
import top.continew.admin.exam.model.req.WeldingOperScoreReq;
import top.continew.admin.exam.model.resp.WeldingOperScoreDetailResp;
import top.continew.admin.exam.model.resp.WeldingOperScoreResp;
import top.continew.admin.exam.service.WeldingOperScoreService;

/**
 * 焊接项目实操成绩管理 API
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Tag(name = "焊接项目实操成绩管理 API")
@RestController
@CrudRequestMapping(value = "/exam/weldingOperScore", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class WeldingOperScoreController extends BaseController<WeldingOperScoreService, WeldingOperScoreResp, WeldingOperScoreDetailResp, WeldingOperScoreQuery, WeldingOperScoreReq> {}