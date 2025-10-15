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

package top.continew.admin.certificate.controller;

import jakarta.annotation.Resource;
import net.dreamlu.mica.core.result.R;
import org.springframework.validation.annotation.Validated;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.certificate.model.query.ReexamineQuery;
import top.continew.admin.certificate.model.req.ReexamineReq;
import top.continew.admin.certificate.model.resp.ReexamineDetailResp;
import top.continew.admin.certificate.model.resp.ReexamineResp;
import top.continew.admin.certificate.service.ReexamineService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

/**
 * 复审管理 API
 *
 * @author Anton
 * @since 2025/04/29 08:48
 */
@Tag(name = "复审管理 API")
@RestController
@CrudRequestMapping(value = "/certificate/reexamine", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ReexamineController extends BaseController<ReexamineService, ReexamineResp, ReexamineDetailResp, ReexamineQuery, ReexamineReq> {

    @Resource
    private ReexamineService reexamineService;

    @GetMapping("/certificates")
    public PageResp<ReexamineResp> certificates(@Validated ReexamineQuery query, @Validated PageQuery pageQuery) {

        return reexamineService.certificates(query, pageQuery);
    }

    // 在ReexamineController中明确接收状态参数
    @PutMapping("/zdyupdate/{id}")
    public R zdyupdate(@RequestBody ReexamineResp request, @PathVariable Long id) {  // 专用请求类

        reexamineService.updateStatus(request, id);
        return R.success();
    }
}