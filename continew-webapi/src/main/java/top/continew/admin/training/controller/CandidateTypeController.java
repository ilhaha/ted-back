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

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.CandidateTypeQuery;
import top.continew.admin.training.model.req.CandidateTypeReq;
import top.continew.admin.training.model.resp.CandidateTypeDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeResp;
import top.continew.admin.training.service.CandidateTypeService;

/**
 * 考生类型管理 API
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Tag(name = "考生类型管理 API")
@RestController
@CrudRequestMapping(value = "/training/candidateType", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class CandidateTypeController extends BaseController<CandidateTypeService, CandidateTypeResp, CandidateTypeDetailResp, CandidateTypeQuery, CandidateTypeReq> {

    /**
     * 切换黑名单状态
     * 
     * @param req
     * @return
     */
    @PostMapping("/blacklist/switch")
    public Boolean blacklistSwitch(@RequestBody CandidateTypeReq req) {
        return baseService.blacklistSwitch(req);
    }
}