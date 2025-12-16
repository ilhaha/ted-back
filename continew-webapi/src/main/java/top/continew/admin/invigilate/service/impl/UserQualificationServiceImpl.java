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

package top.continew.admin.invigilate.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.invigilate.model.dto.UserQualificationDTO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.invigilate.mapper.UserQualificationMapper;
import top.continew.admin.invigilate.model.entity.UserQualificationDO;
import top.continew.admin.invigilate.model.query.UserQualificationQuery;
import top.continew.admin.invigilate.model.req.UserQualificationReq;
import top.continew.admin.invigilate.model.resp.UserQualificationDetailResp;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;
import top.continew.admin.invigilate.service.UserQualificationService;

import java.util.List;

/**
 * 监考员资质证明业务实现
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Service
@RequiredArgsConstructor
public class UserQualificationServiceImpl extends BaseServiceImpl<UserQualificationMapper, UserQualificationDO, UserQualificationResp, UserQualificationDetailResp, UserQualificationQuery, UserQualificationReq> implements UserQualificationService {

    private final UserQualificationMapper userQualificationMapper;

    @Override
    public List<UserQualificationDTO> listByUserId(Long userId) {
        return userQualificationMapper.listByUserId(userId);
    }

    @Override
    public boolean addQualification(UserQualificationReq req) {

        Long count = lambdaQuery().eq(UserQualificationDO::getUserId, req.getUserId())
            .eq(UserQualificationDO::getCategoryId, req.getCategoryId())
            .count();

        if (count != null && count > 0) {
            throw new BusinessException("该用户此类别资质已存在，请勿重复添加");
        }
        UserQualificationDO entity = new UserQualificationDO();
        entity.setUserId(req.getUserId());
        entity.setCategoryId(req.getCategoryId());
        entity.setQualificationUrl(req.getQualificationUrl());

        return this.save(entity);
    }

    @Override
    public boolean hasQualification(Long userId, Long categoryId) {
        Long count = lambdaQuery().eq(UserQualificationDO::getUserId, userId)
            .eq(UserQualificationDO::getCategoryId, categoryId)
            .count();

        return count != null && count > 0;
    }

}