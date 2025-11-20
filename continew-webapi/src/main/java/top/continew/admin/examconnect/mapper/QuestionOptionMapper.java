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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.req.dto.OptionDTO;

import java.util.List;

/**
 * 题目选项 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
public interface QuestionOptionMapper extends BaseMapper<OptionDTO> {

    /**
     * 查询指定题目的所有选项
     */
    List<OptionDTO> selectByQuestionId(@Param("questionId") Long questionId);
}
