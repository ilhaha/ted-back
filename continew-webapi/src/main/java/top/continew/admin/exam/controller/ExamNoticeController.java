/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.document.model.resp.CategoryNoticeTreeVO;
import top.continew.admin.exam.model.query.ExamPlanQuery;
import top.continew.admin.exam.model.req.ExamApplyReq;
import top.continew.admin.exam.model.req.ExamNoticeAuditReq;
import top.continew.admin.exam.model.req.NoticeUploadDocReq;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.system.model.vo.UploadWhenUserInfoVO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamNoticeQuery;
import top.continew.admin.exam.model.req.ExamNoticeReq;
import top.continew.admin.exam.service.ExamNoticeService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 无损检测、检验人员考试通知管理 API
 *
 * @author ilhaha
 * @since 2026/04/14 15:20
 */
@Tag(name = "无损检测、检验人员考试通知管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examNotice", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ExamNoticeController extends BaseController<ExamNoticeService, ExamNoticeResp, ExamNoticeDetailResp, ExamNoticeQuery, ExamNoticeReq> {

    /**
     * 获取通知报名审核列表
     * @param query
     * @param pageQuery
     * @return
     */
    @SaCheckPermission("noticeApply:audit:page")
    @GetMapping("/apply/audit/page")
    public PageResp<ExamNoticeResp> applyAuditPage(ExamNoticeQuery query, PageQuery pageQuery){
        return baseService.applyAuditPage(query,pageQuery);
    }

    /**
     * 撤销报名
     * @param noticeId
     * @return
     */
    @PostMapping("/cancel/apply/{noticeId}")
    public Boolean cancelApply(@PathVariable("noticeId") Long noticeId) {
        return baseService.cancelApply(noticeId);
    }

    /**
     * 上传通知资料
     * @return
     */
    @PostMapping("/upload/submit")
    public Boolean uploadSubmit(@Validated @RequestBody NoticeUploadDocReq noticeUploadDocReq){
        return baseService.uploadSubmit(noticeUploadDocReq);
    }

    /**
     * 根据通知id获取考生已上传的资料列表、未上传的资料列表
     * @param noticeId
     * @return
     */
    @GetMapping("/doc/{noticeId}")
    public NoticeUploadInfoResp getNoticeAndDocInfo(@PathVariable("noticeId") Long noticeId){
        return baseService.getNoticeAndDocInfo(noticeId);
    }

    /**
     * 获取已发布的分类-级别-通知级联选择器
     * @return
     */
    @GetMapping("/tree")
    public List<CategoryNoticeTreeVO> getCategoryNoticeTree(){
        return baseService.getCategoryNoticeTree();
    }

    /**
     * 报考通知
     * @param examApplyReq
     * @return
     */
    @PostMapping("/apply")
    public Boolean apply(@Validated @RequestBody ExamApplyReq examApplyReq) {
        return baseService.apply(examApplyReq);
    }

    /**
     * 检验人员报名时查看通知的详细内容
     * @param noticeId
     * @return
     */
    @GetMapping("/apply/info/{noticeId}")
    public NoticeApplyInfoResp getNoticeApplyInfo(@PathVariable("noticeId") Long noticeId) {
        return baseService.getNoticeApplyInfo(noticeId);
    }

    /**
     * 检验人员查看通知列表
     *
     * @param examNoticeQuery
     * @param pageQuery
     * @return 分页结果
     */
    @SaCheckPermission("examNotice:inspection:page")
    @GetMapping({"/inspection/page"})
    public PageResp<ExamNoticeResp> inspectionGetNoticeList(ExamNoticeQuery examNoticeQuery, @Validated PageQuery pageQuery) {
        return baseService.inspectionGetNoticeList(examNoticeQuery, pageQuery);
    }

    /**
     * 审核
     * 
     * @param req
     * @return
     */
    @PutMapping("/audit")
    public Boolean auditExamNotice(@RequestBody ExamNoticeAuditReq req) {
        return baseService.auditExamNotice(req);
    }
}