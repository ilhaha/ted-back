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

package top.continew.admin.invigilate.controller;

import jakarta.validation.Valid;
import top.continew.admin.invigilate.model.dto.UserQualificationDTO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.invigilate.model.query.UserQualificationQuery;
import top.continew.admin.invigilate.model.req.UserQualificationReq;
import top.continew.admin.invigilate.model.resp.UserQualificationDetailResp;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;
import top.continew.admin.invigilate.service.UserQualificationService;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 监考员资质证明管理 API
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Tag(name = "监考员资质证明管理 API")
@RestController
@CrudRequestMapping(value = "/invigilate/userQualification", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class UserQualificationController extends BaseController<UserQualificationService, UserQualificationResp, UserQualificationDetailResp, UserQualificationQuery, UserQualificationReq> {

    /**
     * 根据用户ID查询资质列表
     *
     * @param userId 用户ID
     * @return 资质列表
     */
    @GetMapping("/list/{userId}")
    public R<List<UserQualificationDTO>> list(@PathVariable Long userId) {
        return R.ok(baseService.listByUserId(userId));
    }

    /**
     * 添加资质证明
     *
     * @param req 资质证明请求体
     * @return 是否添加成功
     */
    @PostMapping("/addQualification")
    public R<Boolean> addQualification(@Valid @RequestBody UserQualificationReq req) {
        return R.ok(baseService.addQualification(req));
    }
}