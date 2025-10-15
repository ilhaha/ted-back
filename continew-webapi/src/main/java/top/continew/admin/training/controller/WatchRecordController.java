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

import org.springframework.validation.annotation.Validated;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.WatchRecordQuery;
import top.continew.admin.training.model.req.WatchRecordReq;
import top.continew.admin.training.model.resp.WatchRecordDetailResp;
import top.continew.admin.training.model.resp.WatchRecordResp;
import top.continew.admin.training.service.WatchRecordService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

/**
 * 学习记录管理 API
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Tag(name = "学习记录管理 API")
@RestController
@CrudRequestMapping(value = "/training/watchRecord", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class WatchRecordController extends BaseController<WatchRecordService, WatchRecordResp, WatchRecordDetailResp, WatchRecordQuery, WatchRecordReq> {

    //重写page，加上考生名和视频名,这里以后再看看要不要改
    @GetMapping("/watchRecord")
    public PageResp<WatchRecordResp> watchRecordPage(@Validated WatchRecordQuery query,
                                                     @Validated PageQuery pageQuery) {
        return baseService.watchRecordPage(query, pageQuery);
    }

    //重写查询方法
    @GetMapping("/watchRecordById/{id}")
    public WatchRecordDetailResp watchRecordById(@PathVariable String id) {
        return baseService.watchRecordById(Long.valueOf(id));
    }
}