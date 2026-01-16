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

package top.continew.admin.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import top.continew.admin.common.model.resp.AddressResp;
import top.continew.admin.common.model.resp.AnnouncementIndexResp;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.model.query.AnnouncementQuery;
import top.continew.admin.common.model.req.AnnouncementReq;
import top.continew.admin.common.model.resp.AnnouncementDetailResp;
import top.continew.admin.common.model.resp.AnnouncementResp;
import top.continew.admin.common.service.AnnouncementService;

import java.util.List;

/**
 * 公告管理管理 API
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Tag(name = "公告管理管理 API")
@RestController
@CrudRequestMapping(value = "/common/announcement", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class AnnouncementController extends BaseController<AnnouncementService, AnnouncementResp, AnnouncementDetailResp, AnnouncementQuery, AnnouncementReq> {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 前端下拉框数据
     */
    @GetMapping("/select")
    public List<AddressResp> select() {
        return baseService.select();
    }

    @GetMapping("/back/detail/{id}")
    public AnnouncementDetailResp getAnnouncementDetail(@PathVariable("id") Long id) {
        return baseService.backDetail(id);
    }

    @Operation(summary = "根据id修改公告状态", description = "修改公告状态")
    @PutMapping("/updateStatusById/{id}/{status}")
    public String updateById(@PathVariable("id") @NotNull Integer id, @PathVariable("status") Integer status) {
        return announcementService.updateById(id, status);
    }

    /**
     * 考生端首页展示
     */
//    @Operation(summary = "考生端首页公告展示API", description = "考生端首页公告展示API")
//    @GetMapping("/index")
//    public List<AnnouncementIndexResp> index() {
//        return announcementService.index();
//    }

//    @Operation(summary = "考生端登录后公告展示API", description = "考生端登录后公告展示API")
//    @GetMapping("/home")
//    public List<AnnouncementIndexResp> home() {
//        return announcementService.home();
//    }

    /**
     * 考生端首页展示
     */
    @Operation(summary = "获取公告详情", description = "获取公告详情")
    @GetMapping("/detail/{id}")
    public AnnouncementDetailResp detail(@PathVariable("id") Long id) {
        return announcementService.detail(id);
    }

}