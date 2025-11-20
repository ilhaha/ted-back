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
import org.apache.ibatis.annotations.Param;
import top.continew.admin.training.model.resp.OrgCandidateResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.OrgCandidateDO;

/**
 * 机构考生关联 Mapper
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
public interface OrgCandidateMapper extends BaseMapper<OrgCandidateDO> {
    IPage<OrgCandidateResp> getCandidatesList(@Param("page") Page<Object> objectPage,
                                              @Param(Constants.WRAPPER) QueryWrapper<OrgCandidateDO> queryWrapper);

    // 修改机构考生表状态
    void updateCandidateStatus(@Param("id") Long id,
                               @Param("candidateId") Long candidateId,
                               @Param("status") Integer status,
                               @Param("remark") String remark,
                               @Param("updateUser") Long updateUser);

}