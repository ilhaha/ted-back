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

package top.continew.admin.exam.service;

import top.continew.admin.document.model.resp.CategoryNoticeTreeVO;
import top.continew.admin.exam.model.req.ExamApplyReq;
import top.continew.admin.exam.model.req.ExamNoticeAuditReq;
import top.continew.admin.exam.model.req.NoticeUploadDocReq;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.system.model.vo.UploadWhenUserInfoVO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamNoticeQuery;
import top.continew.admin.exam.model.req.ExamNoticeReq;

import java.util.List;

/**
 * 无损检测、检验人员考试通知业务接口
 *
 * @author ilhaha
 * @since 2026/04/14 15:20
 */
public interface ExamNoticeService extends BaseService<ExamNoticeResp, ExamNoticeDetailResp, ExamNoticeQuery, ExamNoticeReq> {

    /**
     * 审核
     * 
     * @param req
     * @return
     */
    Boolean auditExamNotice(ExamNoticeAuditReq req);

    /**
     * 检验人员查看通知列表
     *
     * @param examNoticeQuery
     * @param pageQuery
     * @return 分页结果
     */
    PageResp<ExamNoticeResp> inspectionGetNoticeList(ExamNoticeQuery examNoticeQuery, PageQuery pageQuery);

    /**
     * 检验人员报名时查看通知的详细内容
     * @param noticeId
     * @return
     */
    NoticeApplyInfoResp getNoticeApplyInfo(Long noticeId);

    /**
     * 报考通知
     * @param examApplyReq
     * @return
     */
    Boolean apply(ExamApplyReq examApplyReq);

    /**
     * 获取已发布的分类-级别-通知级联选择器
     * @return
     */
    List<CategoryNoticeTreeVO> getCategoryNoticeTree();

    /**
     * 根据通知id获取考生已上传的资料列表、未上传的资料列表
     * @param noticeId
     * @return
     */
    NoticeUploadInfoResp getNoticeAndDocInfo(Long noticeId);

    /**
     * 获取通知对应的项目
     * @param noticeId
     * @return
     */
    List<ProjectResp> getProjectByNoticeId(Long noticeId);

    /**
     * 上传通知资料
     * @return
     */
    Boolean uploadSubmit(NoticeUploadDocReq noticeUploadDocReq);

    /**
     * 撤销报名
     * @param noticeId
     * @return
     */
    Boolean cancelApply(Long noticeId);

    /**
     * 获取通知报名审核列表
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<ExamNoticeResp> applyAuditPage(ExamNoticeQuery query, PageQuery pageQuery);
}