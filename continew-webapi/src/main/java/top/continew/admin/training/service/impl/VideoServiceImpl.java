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

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.training.mapper.ChapterMapper;
import top.continew.admin.training.mapper.TrainingMapper;
import top.continew.admin.training.model.entity.ChapterDO;
import top.continew.admin.training.model.entity.TrainingDO;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.VideoMapper;
import top.continew.admin.training.model.entity.VideoDO;
import top.continew.admin.training.model.query.VideoQuery;
import top.continew.admin.training.model.req.VideoReq;
import top.continew.admin.training.model.resp.VideoDetailResp;
import top.continew.admin.training.model.resp.VideoResp;
import top.continew.admin.training.service.VideoService;

/**
 * 视频业务实现
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends BaseServiceImpl<VideoMapper, VideoDO, VideoResp, VideoDetailResp, VideoQuery, VideoReq> implements VideoService {

    @Resource
    private TrainingMapper trainingMapper;

    @Resource
    private ChapterMapper chapterMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public Boolean customizeSave(VideoReq videoReq) {
        //1. 更新培训总时长
        TrainingDO trainingDO = trainingMapper.selectById(videoReq.getTrainingId());
        trainingDO.setTotalDuration(trainingDO.getTotalDuration() + videoReq.getDuration());
        trainingMapper.updateById(trainingDO);
        //2. 添加视频
        Long trainingId = chapterMapper.selectById(videoReq.getChapterId()).getTrainingId();
        videoReq.setTrainingId(trainingId);
        VideoDO videoDO = new VideoDO();
        BeanUtils.copyProperties(videoReq, videoDO);
        baseMapper.insert(videoDO);
        //3.更新redis的视频总时长
        stringRedisTemplate.opsForValue()
            .increment(RedisConstant.TRAIN_STUDY_TIME_TOTAL + RedisConstant.DELIMITER + videoReq.getTrainingId()
                .toString(), videoReq.getDuration());
        //4.更新培训视频的审核状态为未审核
        TrainingDO trainingDO1 = new TrainingDO();
        trainingDO1.setId(videoReq.getTrainingId());
        trainingDO1.setStatus(0L);
        trainingMapper.updateById(trainingDO1);
        return true;
    }

    @Override
    @Transactional
    public Boolean customizeUpdate(Long id, VideoReq videoReq) {
        // 获取对应的章节
        ChapterDO chapterDO = chapterMapper.selectById(videoReq.getChapterId());
        // 先获取当前的视频时长
        VideoDO videoDODB = baseMapper.selectById(id);
        Integer duration = videoDODB.getDuration();
        // 获取培训总时长
        TrainingDO trainingDO = trainingMapper.selectById(chapterDO.getTrainingId());
        trainingDO.setTotalDuration(trainingDO.getTotalDuration() - duration + videoReq.getDuration());
        // 更新培训总时长
        trainingMapper.updateById(trainingDO);
        // 更新视频
        VideoDO videoDO = new VideoDO();
        BeanUtils.copyProperties(videoReq, videoDO);
        videoDO.setId(id);
        baseMapper.updateById(videoDO);
        return true;
    }

    @Override
    @Transactional
    public Boolean customizeDelete(Long id, Long chapterId) {
        // 获取对应的章节
        ChapterDO chapterDO = chapterMapper.selectById(chapterId);
        // 先获取当前的视频时长
        VideoDO videoDODB = baseMapper.selectById(id);
        Integer duration = videoDODB.getDuration();
        // 获取培训总时长
        TrainingDO trainingDO = trainingMapper.selectById(chapterDO.getTrainingId());
        trainingDO.setTotalDuration(trainingDO.getTotalDuration() - duration);
        // 更新培训总时长
        trainingMapper.updateById(trainingDO);
        // 删除视频
        baseMapper.deleteById(id);
        return true;
    }
}