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

package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.PlanApplyClassMapper;
import top.continew.admin.exam.model.entity.PlanApplyClassDO;
import top.continew.admin.exam.model.query.PlanApplyClassQuery;
import top.continew.admin.exam.model.req.PlanApplyClassReq;
import top.continew.admin.exam.model.resp.PlanApplyClassDetailResp;
import top.continew.admin.exam.model.resp.PlanApplyClassResp;
import top.continew.admin.exam.service.PlanApplyClassService;

/**
 * 考试计划报考班级业务实现
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Service
@RequiredArgsConstructor
public class PlanApplyClassServiceImpl extends BaseServiceImpl<PlanApplyClassMapper, PlanApplyClassDO, PlanApplyClassResp, PlanApplyClassDetailResp, PlanApplyClassQuery, PlanApplyClassReq> implements PlanApplyClassService {

    /**
     * 重写page
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<PlanApplyClassResp> page(PlanApplyClassQuery query, PageQuery pageQuery) {
        QueryWrapper<PlanApplyClassDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tpac.is_deleted", 0);
        IPage<PlanApplyClassDetailResp> page = baseMapper.selectExamPlanPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        PageResp<PlanApplyClassResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}