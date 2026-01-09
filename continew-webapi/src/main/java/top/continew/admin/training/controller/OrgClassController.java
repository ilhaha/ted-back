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

package top.continew.admin.training.controller;

import org.springframework.http.ResponseEntity;
import top.continew.admin.training.model.req.OrgClassPaymentUpdateReq;
import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;
import top.continew.admin.training.service.OrgClassService;

import java.util.List;

/**
 * 培训机构班级管理 API
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Tag(name = "培训机构班级管理 API")
@RestController
@CrudRequestMapping(value = "/training/orgClass", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class OrgClassController extends BaseController<OrgClassService, OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> {

    /**
     * 上传班级缴费凭证
     * @param orgClassPaymentUpdateReq
     * @return
     */
    @PostMapping("/upload/pay/proof")
    public Boolean uploadProof(@RequestBody OrgClassPaymentUpdateReq orgClassPaymentUpdateReq) {
        return baseService.uploadProof(orgClassPaymentUpdateReq);
    }

    /**
     * 下载班级缴费通知单
     * @param classId
     * @return
     */
    @GetMapping("/download/payment/notice/{classId}")
    public ResponseEntity<byte[]> downloadPaymentNotice(@PathVariable("classId") Long classId) {
        return baseService.downloadPaymentNotice(classId);
    }

    /**
     * 班级结束报名
     * @param req
     * @param id
     * @return
     */
    @PutMapping("/end/apply/{id}")
    public Boolean endApply(@RequestBody OrgClassReq req, @PathVariable("id") Long id) {
        return baseService.endApply(req,id);
    }

    /**
     * 根据项目类型和班级类型获取班级选择器
     * orgQueryFlag 1 机构查询 0 后台查询
     * 
     * @param projectId
     * @param classType
     * @return
     */
    @GetMapping("/select/{projectId}/{classType}/{orgQueryFlag}")
    public List<SelectClassVO> getSelectClassByProject(@PathVariable("projectId") Long projectId,
                                                       @PathVariable("classType") Integer classType,
                                                       @PathVariable("orgQueryFlag") Integer orgQueryFlag) {
        return baseService.getSelectClassByProject(projectId, classType, orgQueryFlag);
    }
}