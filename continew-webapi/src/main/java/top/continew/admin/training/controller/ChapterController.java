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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.ChapterQuery;
import top.continew.admin.training.model.req.ChapterReq;
import top.continew.admin.training.model.resp.ChapterDetailResp;
import top.continew.admin.training.model.resp.ChapterResp;
import top.continew.admin.training.service.ChapterService;

/**
 * 章节表管理 API
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Tag(name = "章节表管理 API")
@RestController
@CrudRequestMapping(value = "/training/chapter", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ChapterController extends BaseController<ChapterService, ChapterResp, ChapterDetailResp, ChapterQuery, ChapterReq> {

    @Autowired
    ChapterService chapterService;

    @Operation(summary = "自定义新增章节", description = "自定义新增章节")
    @PostMapping("/customize/save")
    public Boolean customizeSave(@Validated @RequestBody ChapterReq chapterReq) {
        return chapterService.customizeSave(chapterReq);
    }

    @Operation(summary = "自定义修改章节", description = "自定义修改章节")
    @PutMapping("/customize/update/{id}")
    public Boolean customizeUpdate(@Validated @RequestBody ChapterReq chapterReq, @PathVariable("id") Long id) {
        return chapterService.customizeUpdate(chapterReq, id);
    }

    @Operation(summary = "自定义删除章节", description = "自定义删除章节")
    @DeleteMapping("/customize/delete/{id}")
    public Boolean customizeDelete(@Validated @PathVariable("id") Long id) {
        return chapterService.customizeDelete(id);
    }

}