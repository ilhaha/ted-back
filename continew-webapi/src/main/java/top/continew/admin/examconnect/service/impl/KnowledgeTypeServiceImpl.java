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

package top.continew.admin.examconnect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.examconnect.mapper.KnowledgeTypeMapper;
import top.continew.admin.examconnect.model.entity.KnowledgeTypeDO;
import top.continew.admin.examconnect.model.query.KnowledgeTypeQuery;
import top.continew.admin.examconnect.model.req.KnowledgeTypeReq;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeDetailResp;
import top.continew.admin.examconnect.model.resp.KnowledgeTypeResp;
import top.continew.admin.examconnect.service.KnowledgeTypeService;

import java.util.List;

/**
 * 知识类型，存储不同类型的知识占比业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:39
 */
@Service
@RequiredArgsConstructor
public class KnowledgeTypeServiceImpl extends BaseServiceImpl<KnowledgeTypeMapper, KnowledgeTypeDO, KnowledgeTypeResp, KnowledgeTypeDetailResp, KnowledgeTypeQuery, KnowledgeTypeReq> implements KnowledgeTypeService {

    private final Integer FULL_PROPORTION = 100;

    @Override
    public PageResp<KnowledgeTypeResp> page(KnowledgeTypeQuery query, PageQuery pageQuery) {
        QueryWrapper<KnowledgeTypeDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("t1.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<KnowledgeTypeResp> page = baseMapper.selectKnowledgeTypePage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<KnowledgeTypeResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);

        return build;
    }

    @Override
    public void update(KnowledgeTypeReq req, Long id) {
        // 查看该项目的所有分数占比
        List<KnowledgeTypeDO> knowledgeTypes = baseMapper.selectList(new QueryWrapper<KnowledgeTypeDO>()
            .eq("project_id", req.getProjectId())
            .eq("is_deleted", 0));
        Integer total = req.getProportion();
        for (KnowledgeTypeDO knowledgeType : knowledgeTypes)
            total += knowledgeType.getProportion();
        total -= baseMapper.selectById(req.getId()).getProportion();
        ValidationUtils.throwIf(total > FULL_PROPORTION, "操作失败, 占分比总数不能超过100%!");
        super.update(req, id);
    }

    @Override
    @Transactional
    public Long add(KnowledgeTypeReq req) {
        ValidationUtils.throwIf(req.getProportion() > FULL_PROPORTION, "操作失败, 占分比总数不能超过100%!");
        return super.add(req);
    }

}