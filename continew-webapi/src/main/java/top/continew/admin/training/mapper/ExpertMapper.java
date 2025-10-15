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

package top.continew.admin.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import top.continew.admin.expert.model.Expert;
import top.continew.admin.training.model.resp.ExpertResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.ExpertDO;

/**
 * 专家信息 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:45
 */
@Mapper
public interface ExpertMapper extends BaseMapper<ExpertDO> {

    //用身份证判断专家是否存在
    @Select("select count(*) from ted_expert where id_card = #{idCard}")
    int isExpertExist(String idCard);

    //插入专家信息
    @Insert("insert into ted_expert(name,id_card,education,title,avatar) values(#{name},#{idCard},#{education},#{title},#{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertexper(Expert expert);

    IPage<ExpertResp> getExpertList(@Param("page") Page<ExpertDO> page,
                                    @Param(Constants.WRAPPER) QueryWrapper<ExpertDO> queryWrapper);
}