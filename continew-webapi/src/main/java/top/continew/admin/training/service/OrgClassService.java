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

package top.continew.admin.training.service;

import org.springframework.http.ResponseEntity;
import top.continew.admin.exam.model.req.ReviewPaymentReq;
import top.continew.admin.training.model.req.OrgClassPaymentUpdateReq;
import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.admin.worker.model.resp.WorkerApplyResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;

import java.util.List;

/**
 * 培训机构班级业务接口
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
public interface OrgClassService extends BaseService<OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> {
    /**
     * 根据项目类型和班级类型获取班级选择器
     * orgQueryFlag 1 机构查询 0 后台查询
     * 
     * @param projectId
     * @param classType
     * @return
     */
    List<SelectClassVO> getSelectClassByProject(Long projectId, Integer classType, Integer orgQueryFlag);

    /**
     * 班级结束报名
     * @param req
     * @param id
     * @return
     */
    Boolean endApply(OrgClassReq req, Long id);


    /**
     * 下载班级缴费通知单
     * @param classId
     * @return
     */
    ResponseEntity<byte[]> downloadPaymentNotice(Long classId);

    /**
     * 上传班级缴费凭证
     * @param orgClassPaymentUpdateReq
     * @return
     */
    Boolean uploadProof(OrgClassPaymentUpdateReq orgClassPaymentUpdateReq);


    /**
     * 审核班级缴费凭证
     * @param reviewPaymentReq
     * @return
     */
    Boolean reviewUploadProof(ReviewPaymentReq reviewPaymentReq);

    /**
     * 后台根据作业人员班级查询报名信息
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<OrgClassResp> adminQueryWorkerClassPage(OrgClassQuery query, PageQuery pageQuery);

    /**
     * 后台根据作业人员班级查询班级缴费信息
     * @param query
     * @param pageQuery
     * @return
     */
    PageResp<OrgClassResp> adminQueryPayAuditPage(OrgClassQuery query, PageQuery pageQuery);
}