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

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.exam.model.resp.AllPathVo;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.CategoryQuery;
import top.continew.admin.exam.model.req.CategoryReq;
import top.continew.admin.exam.model.resp.CategoryDetailResp;
import top.continew.admin.exam.model.resp.CategoryResp;
import top.continew.admin.exam.service.CategoryService;

import java.util.List;

/**
 * 八大类，存储题目分类信息管理 API
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
@Tag(name = "八大类API")
@RestController
@CrudRequestMapping(value = "/exam/category", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class CategoryController extends BaseController<CategoryService, CategoryResp, CategoryDetailResp, CategoryQuery, CategoryReq> {

    /**
     * 获取下拉框
     */
    @Operation(summary = "获取八大类下拉框")
    @GetMapping("/selectOptions")
    public List<ProjectVo> getSelectOptions() {
        return baseService.getSelectOptions();
    }

    /**
     * 根据最后一个值来查前面的值
     * 
     * @param id
     * @return
     */
    @GetMapping("/getAllPath/{id}")
    public AllPathVo getAllPath(@PathVariable Long id) {
        return baseService.getAllPath(id);
    }

    @PostMapping("/verifyExcel")
    public Boolean verifyExcel(@RequestPart("file") MultipartFile file) {
        baseService.verifyExcel(file);
        return true;
    }
}