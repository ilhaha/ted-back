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

package top.continew.admin.worker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;
import top.continew.admin.worker.model.query.WorkerApplyDocumentQuery;
import top.continew.admin.worker.model.req.WorkerApplyDocumentReq;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentResp;
import top.continew.admin.worker.service.WorkerApplyDocumentService;

/**
 * 作业人员报名上传的资料业务实现
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Service
@RequiredArgsConstructor
public class WorkerApplyDocumentServiceImpl extends BaseServiceImpl<WorkerApplyDocumentMapper, WorkerApplyDocumentDO, WorkerApplyDocumentResp, WorkerApplyDocumentDetailResp, WorkerApplyDocumentQuery, WorkerApplyDocumentReq> implements WorkerApplyDocumentService {

    /**
     * 重写page
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<WorkerApplyDocumentResp> page(WorkerApplyDocumentQuery query, PageQuery pageQuery) {
        QueryWrapper<WorkerApplyDocumentDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("twad.is_deleted", 0);
        IPage<WorkerApplyDocumentDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        PageResp<WorkerApplyDocumentResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}