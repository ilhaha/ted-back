package top.continew.admin.training.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;
import top.continew.admin.training.service.TrainingCheckinService;

/**
 * 培训签到记录管理 API
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Tag(name = "培训签到记录管理 API")
@RestController
@CrudRequestMapping(value = "/training/trainingCheckin", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class TrainingCheckinController extends BaseController<TrainingCheckinService, TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> {}