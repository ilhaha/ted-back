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
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.examconnect.model.entity.KnowledgeTypeDO;

/**
 * 知识类型，存储不同类型的知识占比 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:39
 */
@Mapper
public interface KnowledgeTypeMapper extends BaseMapper<KnowledgeTypeDO> {

    IPage<KnowledgeTypeResp> selectKnowledgeTypePage(@Param("page") Page<Object> objectPage,
                                                     @Param(Constants.WRAPPER) QueryWrapper<KnowledgeTypeDO> queryWrapper);

    @Select("select id from ted_knowledge_type where name = #{knowledgeTypeName}")
    Long selectIdByKnowledgeTypeName(@Param("knowledgeTypeName") String knowledgeTypeName);
}