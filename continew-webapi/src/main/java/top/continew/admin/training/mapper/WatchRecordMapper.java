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

package top.continew.admin.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.training.model.resp.WatchRecordResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.WatchRecordDO;

import java.util.List;

/**
 * 学习记录 Mapper
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */

public interface WatchRecordMapper extends BaseMapper<WatchRecordDO> {
    List<WatchRecordDO> findExistingRecords(List<WatchRecordDO> list);

    IPage<WatchRecordResp> getWatchRecord(@Param("page") Page<WatchRecordDO> page,
                                          @Param(Constants.WRAPPER) QueryWrapper<WatchRecordDO> queryWrapper);

    WatchRecordDO watchRecordById(Long id);

    /**
     * 批量更新
     * 
     * @param list
     */
    void batchUpdateByUniqueKey(@Param("list") List<WatchRecordDO> list);
}