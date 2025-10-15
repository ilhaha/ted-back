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

package top.continew.admin.common.service;

import top.continew.admin.common.model.resp.AddressResp;
import top.continew.admin.common.model.resp.AnnouncementIndexResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.common.model.query.AnnouncementQuery;
import top.continew.admin.common.model.req.AnnouncementReq;
import top.continew.admin.common.model.resp.AnnouncementDetailResp;
import top.continew.admin.common.model.resp.AnnouncementResp;

import java.util.List;

/**
 * 公告管理业务接口
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
public interface AnnouncementService extends BaseService<AnnouncementResp, AnnouncementDetailResp, AnnouncementQuery, AnnouncementReq> {

    String updateById(Integer id, Integer status);

    /**
     * 首页展示十条
     */
    List<AnnouncementIndexResp> index();

    AnnouncementDetailResp detail(Long id);

    /**
     * 后台管理查看详情
     */
    AnnouncementDetailResp backDetail(Long id);

    /**
     * 查询所有地址 前端下拉框使用
     */
    List<AddressResp> select();

    /**
     * 展示五条
     */
    List<AnnouncementIndexResp> home();
}