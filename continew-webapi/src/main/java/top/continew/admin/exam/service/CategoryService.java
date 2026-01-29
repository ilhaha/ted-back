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

package top.continew.admin.exam.service;

import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.exam.model.resp.AllPathVo;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.CategoryQuery;
import top.continew.admin.exam.model.req.CategoryReq;
import top.continew.admin.exam.model.resp.CategoryDetailResp;
import top.continew.admin.exam.model.resp.CategoryResp;

import java.util.List;

/**
 * 八大类，存储题目分类信息业务接口
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
public interface CategoryService extends BaseService<CategoryResp, CategoryDetailResp, CategoryQuery, CategoryReq> {
    /**
     * 下拉框
     * 
     * @return
     */
    List<ProjectVo> getSelectOptions(List<Integer> categoryType);

    /**
     * 根据最后一个值来查前面的值
     * 
     * @param id
     * @return
     */
    AllPathVo getAllPath(Long id);

    Boolean verifyExcel(MultipartFile file);
}