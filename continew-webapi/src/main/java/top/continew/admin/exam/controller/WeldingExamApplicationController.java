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
@CrudRequestMapping(value = "/exam/weldingExamApplication", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class WeldingExamApplicationController extends BaseController<WeldingExamApplicationService, WeldingExamApplicationResp, WeldingExamApplicationDetailResp, WeldingExamApplicationQuery, WeldingExamApplicationReq> {

    /**
     * 审核
     * 
     * @param req
     * @return
     */
    @SaCheckPermission("exam:weldingExamApplication:review")
    @PostMapping("/review")
    public Boolean review(@RequestBody ReviewWeldingExamApplicationReq req) {
        return baseService.review(req);
    }
}