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

package top.continew.admin.examconnect.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.examconnect.model.resp.QuestionBankResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.examconnect.model.entity.QuestionBankDO;

import java.util.List;
import java.util.Map;

/**
 * 题库，存储各类题目及其分类信息 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
public interface QuestionBankMapper extends BaseMapper<QuestionBankDO> {

    IPage<QuestionBankResp> selectQuestionBankPage(@Param("page") Page<Object> objectPage,
                                                   @Param(Constants.WRAPPER) QueryWrapper<QuestionBankDO> queryWrapper);

    /**
     * 根据分类条件查询题目
     */
    List<QuestionBankResp> selectByCategory(@Param("categoryId") Long categoryId,
                                            @Param("subCategoryId") Long subCategoryId,
                                            @Param("knowledgeTypeId") Long knowledgeTypeId);

    List<Map<String, Object>> selectExistingQuestions(@Param("questionTexts") List<String> questionTexts,
                                                      @Param("categoryId") Long categoryId,
                                                      @Param("projectId") Long projectId,
                                                      @Param("knowledgeTypeId") Long knowledgeTypeId);

}