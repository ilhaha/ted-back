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

package top.continew.admin.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.common.model.resp.AddressResp;
import top.continew.admin.common.model.resp.AnnouncementDetailResp;
import top.continew.admin.common.model.resp.AnnouncementIndexResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.common.model.entity.AnnouncementDO;

import java.util.List;

/**
 * 公告管理 Mapper
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
public interface AnnouncementMapper extends BaseMapper<AnnouncementDO> {

    @Select("SELECT id AS uri, title FROM ted_announcement WHERE is_deleted = 0 AND `status` = 1 ORDER BY create_time DESC, update_time DESC, id DESC LIMIT 10;")
    List<AnnouncementIndexResp> index();

    IPage<AnnouncementDetailResp> selectAnnouncementPage(@Param("page") IPage<AnnouncementDO> page,
                                                         @Param(Constants.WRAPPER) QueryWrapper<AnnouncementDO> queryWrapper);

    @Select("SELECT id AS `value`, title AS `label` FROM ted_announcement WHERE is_deleted = 0 AND `status` = 1 ORDER BY create_time DESC, update_time DESC, id DESC;")
    List<AddressResp> selectAll();

    @Select("SELECT id AS uri, title FROM ted_announcement WHERE is_deleted = 0 AND `status` = 1 ORDER BY create_time DESC, update_time DESC, id DESC LIMIT 5;")
    List<AnnouncementIndexResp> home();

    @Select("SELECT * FROM ted_announcement WHERE id = #{id} AND is_deleted = 0 AND `status` = 1")
    AnnouncementDO selectAnnouncementById(Long id);
}