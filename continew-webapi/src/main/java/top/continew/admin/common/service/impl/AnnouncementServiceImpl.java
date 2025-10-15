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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.common.mapper.CarouselMapper;
import top.continew.admin.common.model.entity.CarouselDO;
import top.continew.admin.common.model.resp.AddressResp;
import top.continew.admin.common.model.resp.AnnouncementIndexResp;
import top.continew.admin.util.TextCompressor;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.common.mapper.AnnouncementMapper;
import top.continew.admin.common.model.entity.AnnouncementDO;
import top.continew.admin.common.model.query.AnnouncementQuery;
import top.continew.admin.common.model.req.AnnouncementReq;
import top.continew.admin.common.model.resp.AnnouncementDetailResp;
import top.continew.admin.common.model.resp.AnnouncementResp;
import top.continew.admin.common.service.AnnouncementService;

import java.io.IOException;
import java.util.List;

/**
 * 公告管理业务实现
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends BaseServiceImpl<AnnouncementMapper, AnnouncementDO, AnnouncementResp, AnnouncementDetailResp, AnnouncementQuery, AnnouncementReq> implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    private final TextCompressor textCompressor;

    private final CarouselMapper carouselMapper;

    @Override
    public String updateById(Integer id, Integer status) {
        ValidationUtils.throwIfNull(id, "公告ID不能为空");
        ValidationUtils.throwIfNull(status, "公告状态不能为空");
        AnnouncementDO announcementDO = announcementMapper.selectById(id);
        ValidationUtils.throwIfNull(announcementDO, "公告不存在");
        announcementDO.setStatus(status);
        announcementMapper.updateById(announcementDO);

        //如果公告状态更新了 那么使用了公告的轮播图都需要更新状态
        if (status != null) {
            QueryWrapper<CarouselDO> carouselDOQueryWrapper = new QueryWrapper<>();
            carouselDOQueryWrapper.eq("announcement_id", id);
            List<CarouselDO> carouselDOS = carouselMapper.selectList(carouselDOQueryWrapper);
            for (CarouselDO carouselDO : carouselDOS) {
                UpdateWrapper<CarouselDO> carouselDOUpdateWrapper = new UpdateWrapper<>();
                carouselDOUpdateWrapper.set("status", status).eq("id", carouselDO.getId());
                carouselMapper.update(carouselDOUpdateWrapper);
            }
        }
        return "根据id修改状态成功";
    }

    @Override
    public List<AnnouncementIndexResp> index() {
        return announcementMapper.index();
    }

    @Override
    public AnnouncementDetailResp detail(Long id) {
        //        AnnouncementDO announcementDO = announcementMapper.selectAnnouncementById(id);
        AnnouncementDO announcementDO = baseMapper.selectById(id);
        ValidationUtils.throwIf(announcementDO.getIsDeleted(), "公告不存在");
        ValidationUtils.throwIf(announcementDO.getStatus() != 1, "公告已下架");
        AnnouncementDetailResp resp = new AnnouncementDetailResp();
        resp.setTitle(announcementDO.getTitle());
        resp.setContent(announcementDO.getContent());
        resp.setCreateTime(announcementDO.getCreateTime());
        return resp;
    }

    @Override
    public AnnouncementDetailResp backDetail(Long id) {
        return super.get(id);
    }

    @Override
    public List<AddressResp> select() {
        return baseMapper.selectAll();
    }

    @Override
    public List<AnnouncementIndexResp> home() {
        return announcementMapper.home();
    }

    @Override
    public PageResp<AnnouncementResp> page(AnnouncementQuery query, PageQuery pageQuery) {
        QueryWrapper<AnnouncementDO> queryWrapper = this.buildQueryWrapper(query);
        super.sort(queryWrapper, pageQuery);
        queryWrapper.eq("is_deleted", 0);
        IPage<AnnouncementDetailResp> page = baseMapper.selectAnnouncementPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        PageResp<AnnouncementResp> pageResp = PageResp.build(page, super.getListClass());
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    @Override
    public Long add(AnnouncementReq req) {
        //        //1. 中小文本 (0-100KB)：LONGTEXT原始存储
        //        String content = req.getContent();
        //        //2. 中型文本 (100KB-20MB)：LONGTEXT+压缩Base64
        //        //TODO  3. 大型文本 (>20MB)：BLOB+缓存策略
        String content = req.getContent();
        try {
            //1.压缩
            String compress = TextCompressor.compress(content);
            req.setContent(compress);
        } catch (IOException e) {
            throw new BusinessException("公告更新失败，请稍后重试");
        }
        return super.add(req);
    }

    @Override
    public void update(AnnouncementReq req, Long id) {
        String content = req.getContent();
        if (content != null) {
            try {
                req.setContent(TextCompressor.compress(content));
            } catch (IOException e) {
                throw new BusinessException("公告更新失败，请稍后重试");
            }
        }
        super.update(req, id);

    }

    @Override
    public void delete(List<Long> ids) {
        super.delete(ids);
        //如果公告被删除了 那么使用了公告的轮播图都需要被删除
        for (Long id : ids) {
            QueryWrapper<CarouselDO> carouselDOQueryWrapper = new QueryWrapper<>();
            carouselDOQueryWrapper.eq("announcement_id", id);
            List<CarouselDO> carouselDOS = carouselMapper.selectList(carouselDOQueryWrapper);
            for (CarouselDO carouselDO : carouselDOS) {
                UpdateWrapper<CarouselDO> carouselDOUpdateWrapper = new UpdateWrapper<>();
                carouselDOUpdateWrapper.set("is_deleted", 1).eq("id", carouselDO.getId());
                carouselMapper.update(carouselDOUpdateWrapper);
            }
        }
    }
}