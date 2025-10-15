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

package top.continew.admin.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.common.model.resp.CarouselIndexResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.common.mapper.CarouselMapper;
import top.continew.admin.common.model.entity.CarouselDO;
import top.continew.admin.common.model.query.CarouselQuery;
import top.continew.admin.common.model.req.CarouselReq;
import top.continew.admin.common.model.resp.CarouselDetailResp;
import top.continew.admin.common.model.resp.CarouselResp;
import top.continew.admin.common.service.CarouselService;

import java.util.List;

/**
 * 轮播图管理业务实现
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Service
@RequiredArgsConstructor
public class CarouselServiceImpl extends BaseServiceImpl<CarouselMapper, CarouselDO, CarouselResp, CarouselDetailResp, CarouselQuery, CarouselReq> implements CarouselService {

    @Override
    public PageResp<CarouselResp> page(CarouselQuery query, PageQuery pageQuery) {
        QueryWrapper<CarouselDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("is_deleted", 0);
        super.sort(queryWrapper, pageQuery);
        IPage<CarouselDetailResp> page = baseMapper.selectCarousePage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        PageResp<CarouselResp> pageResp = PageResp.build(page, super.getListClass());
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    @Override
    public List<CarouselIndexResp> index() {
        return baseMapper.index();
    }

    @Override
    public CarouselDetailResp get(Long id) {
        return baseMapper.getContainAnnouncement(id);
    }
}