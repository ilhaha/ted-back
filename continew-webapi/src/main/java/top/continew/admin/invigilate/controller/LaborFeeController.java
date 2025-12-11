package top.continew.admin.invigilate.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.invigilate.model.query.LaborFeeQuery;
import top.continew.admin.invigilate.model.req.LaborFeeReq;
import top.continew.admin.invigilate.model.resp.LaborFeeDetailResp;
import top.continew.admin.invigilate.model.resp.LaborFeeResp;
import top.continew.admin.invigilate.service.LaborFeeService;

/**
 * 考试劳务费配置管理 API
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Tag(name = "考试劳务费配置管理 API")
@RestController
@CrudRequestMapping(value = "/invigilate/laborFee", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class LaborFeeController extends BaseController<LaborFeeService, LaborFeeResp, LaborFeeDetailResp, LaborFeeQuery, LaborFeeReq> {}