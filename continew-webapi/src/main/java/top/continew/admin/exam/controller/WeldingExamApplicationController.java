package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import top.continew.admin.exam.model.req.ReviewWeldingExamApplicationReq;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.WeldingExamApplicationQuery;
import top.continew.admin.exam.model.req.WeldingExamApplicationReq;
import top.continew.admin.exam.model.resp.WeldingExamApplicationDetailResp;
import top.continew.admin.exam.model.resp.WeldingExamApplicationResp;
import top.continew.admin.exam.service.WeldingExamApplicationService;

/**
 * 机构申请焊接考试项目管理 API
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Tag(name = "机构申请焊接考试项目管理 API")
@RestController
@CrudRequestMapping(value = "/exam/weldingExamApplication", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class WeldingExamApplicationController extends BaseController<WeldingExamApplicationService, WeldingExamApplicationResp, WeldingExamApplicationDetailResp, WeldingExamApplicationQuery, WeldingExamApplicationReq> {


    /**
     * 审核
     * @param req
     * @return
     */
    @SaCheckPermission("exam:weldingExamApplication:review")
    @PostMapping("/review")
    public Boolean review(@RequestBody ReviewWeldingExamApplicationReq req) {
        return baseService.review(req);
    }
}