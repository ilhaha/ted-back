package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import top.continew.admin.exam.model.query.ExamNoticeQuery;
import top.continew.admin.exam.model.resp.ExamNoticeResp;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyResp;
import top.continew.admin.exam.service.ExamineeNoticeApplyService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

/**
 * 考生资料关系管理 API
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Tag(name = "考生资料关系管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examineeNoticeApply", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamineeNoticeApplyController extends BaseController<ExamineeNoticeApplyService, ExamineeNoticeApplyResp, ExamineeNoticeApplyDetailResp, ExamineeNoticeApplyQuery, ExamineeNoticeApplyReq> {

    /**
     * 获取通知对应的考生报名列表
     * @param query
     * @param pageQuery
     * @return
     */
    @SaCheckPermission("noticeApply:audit:detail")
    @GetMapping("/candidate/page")
    public PageResp<ExamineeNoticeApplyResp> getNoticeApplyCandidatePage(ExamineeNoticeApplyQuery query, PageQuery pageQuery){
        return baseService.getNoticeApplyCandidatePage(query,pageQuery);
    }
}