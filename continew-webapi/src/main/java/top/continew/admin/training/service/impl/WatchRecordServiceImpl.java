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
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import top.continew.admin.training.mapper.VideoMapper;
import top.continew.admin.training.model.entity.VideoDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.WatchRecordMapper;
import top.continew.admin.training.model.entity.WatchRecordDO;
import top.continew.admin.training.model.query.WatchRecordQuery;
import top.continew.admin.training.model.req.WatchRecordReq;
import top.continew.admin.training.model.resp.WatchRecordDetailResp;
import top.continew.admin.training.model.resp.WatchRecordResp;
import top.continew.admin.training.service.WatchRecordService;

import java.util.concurrent.TimeUnit;

/**
 * 学习记录业务实现
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Service
@RequiredArgsConstructor
public class WatchRecordServiceImpl extends BaseServiceImpl<WatchRecordMapper, WatchRecordDO, WatchRecordResp, WatchRecordDetailResp, WatchRecordQuery, WatchRecordReq> implements WatchRecordService {

    private final VideoMapper videoMapper;
    private final RedisTemplate redisTemplate;

    @Override
    public PageResp<WatchRecordResp> watchRecordPage(WatchRecordQuery query, PageQuery pageQuery) {
        QueryWrapper<WatchRecordDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("twr.is_deleted", 0);

        // 根据 pageQuery 里的排序参数，对查询结果进行排序
        super.sort(queryWrapper, pageQuery);

        // 执行分页查询
        IPage<WatchRecordResp> page = baseMapper.getWatchRecord(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        // 将查询结果转换成 PageResp 对象
        PageResp<WatchRecordResp> pageResp = PageResp.build(page, super.getListClass());
        // 遍历查询结果列表，调用 fill 方法填充额外字段或处理数据
        pageResp.getList().forEach(this::fill);

        return pageResp;
    }

    @Override
    public WatchRecordDetailResp watchRecordById(Long id) {
        WatchRecordDO examRecords = baseMapper.watchRecordById(id);
        WatchRecordDetailResp detail = BeanUtil.toBean(examRecords, this.getDetailClass());
        this.fill(detail);
        return detail;
    }

    // 重写修改逻辑判断
    @Override
    public void update(WatchRecordReq req, Long id) {
        // 1. 查询视频总时长（缓存优化）
        Integer totalDuration = GetDurationByRecordId(req.getVideoId());
        // 2. 校验
        if (req.getWatchedDuration() > totalDuration) {
            throw new BusinessException("观看时长不能超过视频总时长：" + totalDuration + "秒");
        }
        if (req.getWatchedDuration() != totalDuration) {
            req.setStatus(1);
        }

        if (req.getWatchedDuration() == totalDuration) {
            req.setStatus(2);
        }
        if (req.getWatchedDuration() == 0) {
            req.setStatus(0);
        }
        // 3. 更新数据库
        super.update(req, id);
    }

    //用来缓存时间的
    public int GetDurationByRecordId(Long recordId) {
        //判断在redis中是否存在该记录的观看时长
        String watchedDurationKey = "watch_record_" + recordId;
        Integer watchedDuration = (Integer)redisTemplate.opsForValue().get(watchedDurationKey);
        if (watchedDuration != null) {
            return watchedDuration;
        }
        VideoDO videoDO = videoMapper.selectById(recordId);
        Integer totalDuration = videoDO.getDuration();
        redisTemplate.opsForValue().set(watchedDurationKey, totalDuration);
        redisTemplate.expire(watchedDurationKey, 10, TimeUnit.MINUTES);

        return totalDuration;
    }
}