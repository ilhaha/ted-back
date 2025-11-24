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
    @Transactional(rollbackFor = Exception.class)
    public Long add(KnowledgeTypeReq req) {
        validateProportionSum(req, null);
        return super.add(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(KnowledgeTypeReq req, Long id) {
        validateProportionSum(req, id);
        super.update(req, id);
    }

    /**
     * 校验某项目所有知识类型的占比总和是否超过 100%（新增、修改通用）
     */
    private void validateProportionSum(KnowledgeTypeReq req, Long currentId) {
        // 查出该项目的所有占比
        List<KnowledgeTypeDO> knowledgeTypes = baseMapper.selectList(new QueryWrapper<KnowledgeTypeDO>()
            .eq("project_id", req.getProjectId())
            .eq("is_deleted", 0));

        int total = req.getProportion();

        // 累计已有的占比
        for (KnowledgeTypeDO item : knowledgeTypes) {
            total += item.getProportion();
        }

        // 如果是更新，需要减去旧值的占比
        if (currentId != null) {
            Integer old = baseMapper.selectById(currentId).getProportion();
            total -= old;
        }

        ValidationUtils.throwIf(total > FULL_PROPORTION, "项目所有知识类型的分数占比之和不能超过 100%！");
    }

}