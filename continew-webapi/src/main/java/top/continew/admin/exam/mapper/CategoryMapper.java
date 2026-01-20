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

package top.continew.admin.exam.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.CategoryDO;

import java.util.List;

/**
 * 八大类，存储题目分类信息 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryDO> {
    @Select("SELECT `name` AS label, `id` AS `value` FROM ted_category WHERE is_deleted = 0")
    List<ProjectVo> getSelectOptions();


    @Select("SELECT COUNT(*) FROM ted_knowledge_type tkt " + "inner join ted_project tp on tkt.project_id = tp.id " + "inner join ted_category tc on tp.category_id = tc.id " + "where tc.name = #{categoryName} and tc.code = #{categoryCode} and tp.project_code = #{projectCode} and tp.project_name = #{projectName} and tkt.name = #{knowledgeTypeName} ")
    Long verifySheet(String categoryName,
                     String categoryCode,
                     String projectName,
                     String projectCode,
                     String knowledgeTypeName);

    @Select("select id from ted_category where name = #{CategoryName}")
    Long selectIdByCategoryName(@Param("CategoryName") String CategoryName);
}