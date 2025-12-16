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

package top.continew.admin.invigilate.service;

import top.continew.admin.invigilate.model.dto.UserQualificationDTO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.invigilate.model.query.UserQualificationQuery;
import top.continew.admin.invigilate.model.req.UserQualificationReq;
import top.continew.admin.invigilate.model.resp.UserQualificationDetailResp;
import top.continew.admin.invigilate.model.resp.UserQualificationResp;

import java.util.List;

/**
 * 监考员资质证明业务接口
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
public interface UserQualificationService extends BaseService<UserQualificationResp, UserQualificationDetailResp, UserQualificationQuery, UserQualificationReq> {

    /**
     * 根据用户ID查询资质列表
     */
    List<UserQualificationDTO> listByUserId(Long userId);

    /**
     * 添加资质证明
     */
    boolean addQualification(UserQualificationReq req);

    /**
     * 判断用户是否拥有某类别资质
     */
    boolean hasQualification(Long userId, Long categoryId);

}