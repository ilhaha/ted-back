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

package top.continew.admin.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.document.mapper.DocumentPreMapper;
import top.continew.admin.document.model.entity.DocumentPreDO;
import top.continew.admin.document.model.query.DocumentPreQuery;
import top.continew.admin.document.model.req.DocumentPreReq;
import top.continew.admin.document.model.resp.DocumentPreDetailResp;
import top.continew.admin.document.model.resp.DocumentPreResp;
import top.continew.admin.document.service.DocumentPreService;

/**
 * 机构报考-考生上传资料业务实现
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Service
@RequiredArgsConstructor
public class DocumentPreServiceImpl extends BaseServiceImpl<DocumentPreMapper, DocumentPreDO, DocumentPreResp, DocumentPreDetailResp, DocumentPreQuery, DocumentPreReq> implements DocumentPreService {

    /**
     * 重写page
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<DocumentPreResp> page(DocumentPreQuery query, PageQuery pageQuery) {
        QueryWrapper<DocumentPreDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tdp.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<DocumentPreDetailResp> page = baseMapper.selectDocumentPrePage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<DocumentPreResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}