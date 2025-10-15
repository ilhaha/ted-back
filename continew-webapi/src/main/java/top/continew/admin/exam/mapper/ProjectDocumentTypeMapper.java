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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ProjectDocumentTypeDO;

import java.util.List;

/**
 * 项目与资料类型关联 Mapper
 *
 * @author Anton
 * @since 2025/03/14 11:55
 */
public interface ProjectDocumentTypeMapper extends BaseMapper<ProjectDocumentTypeDO> {

    /**
     * 获取所有资料
     */
    @Select("select * from ted_project_document_type where project_id = #{projectId} and (is_deleted = 0 or is_deleted = 1)")
    List<ProjectDocumentTypeDO> selectAll(@Param("projectId") Long projectId);

    @Update("update ted_project_document_type set is_deleted = 0 where id = #{id}")
    void updateDelStatus(Long id);
}