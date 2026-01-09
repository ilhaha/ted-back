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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.vo.ProjectCategoryVO;
import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.OrgClassDO;

import java.util.List;

/**
 * 培训机构班级 Mapper
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
public interface OrgClassMapper extends BaseMapper<OrgClassDO> {
    IPage<OrgClassDetailResp> page(@Param("page") Page page, @Param("ew") QueryWrapper<OrgClassDO> queryWrapper);

    List<ProjectCategoryVO> getSelectClassByProjectIds(List<Long> projectIds);

    List<SelectClassVO> getSelectClassByProject(@Param("projectId") Long projectId,
                                                @Param("classType") Integer classType,
                                                @Param("orgId") Long orgId);

    IPage<OrgClassDetailResp> workerClassPage(@Param("page") Page page,
                                              @Param("ew") QueryWrapper<OrgClassDO> queryWrapper);

    IPage<OrgClassDetailResp> adminQueryWorkerClassPage(@Param("page") Page page, @Param("ew") QueryWrapper<OrgClassDO> queryWrapper);

    IPage<OrgClassDetailResp> adminQueryPayAuditPage(@Param("page") Page page, @Param("ew") QueryWrapper<OrgClassDO> queryWrapper);
}