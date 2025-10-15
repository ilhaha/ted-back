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
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.common.model.resp.CarouselDetailResp;
import top.continew.admin.common.model.resp.CarouselIndexResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.common.model.entity.CarouselDO;

import java.util.List;

/**
 * 轮播图管理 Mapper
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Mapper
public interface CarouselMapper extends BaseMapper<CarouselDO> {

    @Select("SELECT image_url, image_min_url, announcement_id FROM ted_carousel WHERE is_deleted = 0 AND `status` = 1 ORDER BY sort_order desc LIMIT 5;")
    List<CarouselIndexResp> index();

    IPage<CarouselDetailResp> selectCarousePage(@Param("page") IPage<CarouselDO> page,
                                                @Param(Constants.WRAPPER) QueryWrapper<CarouselDO> queryWrapper);

    CarouselDetailResp getContainAnnouncement(@Param("id") Long id);
}