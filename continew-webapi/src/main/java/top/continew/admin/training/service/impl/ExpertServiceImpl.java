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

package top.continew.admin.training.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.training.mapper.OrgExpertMapper;
import top.continew.admin.training.model.entity.OrgExpertDO;
import top.continew.admin.training.service.OrgService;
import top.continew.admin.util.RedisUtil;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.ExpertMapper;
import top.continew.admin.training.model.entity.ExpertDO;
import top.continew.admin.training.model.query.ExpertQuery;
import top.continew.admin.training.model.req.ExpertReq;
import top.continew.admin.training.model.resp.ExpertDetailResp;
import top.continew.admin.training.model.resp.ExpertResp;
import top.continew.admin.training.service.ExpertService;

import java.util.List;

/**
 * 专家信息业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:45
 */
@Service
@RequiredArgsConstructor
public class ExpertServiceImpl extends BaseServiceImpl<ExpertMapper, ExpertDO, ExpertResp, ExpertDetailResp, ExpertQuery, ExpertReq> implements ExpertService {

    @Resource
    private ExpertMapper expertMapper;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private OrgExpertMapper orgExpertMapper;

    @Resource
    private OrgService orgService;

    @Override
    public PageResp<ExpertResp> page(ExpertQuery query, PageQuery pageQuery) {
        PageResp<ExpertResp> pageResp;
        QueryWrapper<ExpertDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("e.is_deleted", 0).eq("o.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);
        pageResp = (PageResp<ExpertResp>)redisUtil.getStringToClass(RedisConstant.EXAM_EXPERT_QUERY, PageResp.class);
        if (BeanUtil.isEmpty(query) && pageResp == null) {
            IPage<ExpertResp> iPage = baseMapper.getExpertList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
            pageResp = PageResp.build(iPage, super.getListClass());
            pageResp.getList().forEach(this::fill);
            redisUtil.setString(RedisConstant.EXAM_EXPERT_QUERY, pageResp);
            return pageResp;
        }
        return pageResp;
    }

    @Override
    public ExpertDetailResp get(Long id) {
        return super.get(id);
    }

    @Override
    public Long add(ExpertReq req) {
        Long add = super.add(req);
        redisUtil.delete(RedisConstant.EXAM_EXPERT_QUERY);
        return add;
    }

    @Override
    public void update(ExpertReq req, Long id) {
        super.update(req, id);
        redisUtil.delete(RedisConstant.EXAM_EXPERT_QUERY);
    }

    @Override
    public void delete(List<Long> ids) {
        super.delete(ids);
        redisUtil.delete(RedisConstant.EXAM_EXPERT_QUERY);
        //1.删除机构专家关联表
        QueryWrapper<OrgExpertDO> orgExpertQueryWrapper = new QueryWrapper<>();
        orgExpertQueryWrapper.in("expert_id", ids);
        orgExpertMapper.delete(orgExpertQueryWrapper);
    }

    @Override
    public ExpertDetailResp getExpertById(Long id) {
        ValidationUtils.throwIfNull(id, "专家ID不能为空");
        ExpertDetailResp resp = super.get(id);
        String orgName = orgExpertMapper.getOrgName(id);
        resp.setOrgName(orgName);
        return resp;
    }
}