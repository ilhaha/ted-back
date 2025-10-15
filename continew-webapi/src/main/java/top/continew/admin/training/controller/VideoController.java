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

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.VideoQuery;
import top.continew.admin.training.model.req.VideoReq;
import top.continew.admin.training.model.resp.VideoDetailResp;
import top.continew.admin.training.model.resp.VideoResp;
import top.continew.admin.training.service.VideoService;

/**
 * 视频管理 API
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Tag(name = "视频管理 API")
@RestController
@CrudRequestMapping(value = "/training/video", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class VideoController extends BaseController<VideoService, VideoResp, VideoDetailResp, VideoQuery, VideoReq> {

    @Operation(summary = "自定义新增视频", description = "自定义新增视频")
    @PostMapping("/customize/save")
    public Boolean customizeSave(@Validated @RequestBody VideoReq videoReq) {
        return baseService.customizeSave(videoReq);
    }

    @Operation(summary = "自定义修改视频", description = "自定义修改视频")
    @PutMapping("/customize/update/{id}")
    public Boolean customizeUpdate(@Validated @RequestBody VideoReq videoReq, @PathVariable("id") Long id) {
        return baseService.customizeUpdate(id, videoReq);
    }

    @Operation(summary = "自定义删除视频", description = "自定义删除视频")
    @DeleteMapping("/customize/delete/{id}/{chapterId}")
    public Boolean customizeDelete(@Validated @PathVariable("id") Long id, @PathVariable("chapterId") Long chapterId) {
        return baseService.customizeDelete(id, chapterId);
    }

}