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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.training.mapper.VideoMapper;
import top.continew.admin.training.model.entity.VideoDO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.ChapterMapper;
import top.continew.admin.training.model.entity.ChapterDO;
import top.continew.admin.training.model.query.ChapterQuery;
import top.continew.admin.training.model.req.ChapterReq;
import top.continew.admin.training.model.resp.ChapterDetailResp;
import top.continew.admin.training.model.resp.ChapterResp;
import top.continew.admin.training.service.ChapterService;

import java.util.Arrays;
import java.util.List;

/**
 * 章节表业务实现
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl extends BaseServiceImpl<ChapterMapper, ChapterDO, ChapterResp, ChapterDetailResp, ChapterQuery, ChapterReq> implements ChapterService {

    @Resource
    private VideoMapper videoMapper;

    @Override
    @Transactional
    public Boolean customizeSave(ChapterReq chapterReq) {
        // 判断该培训机构下的章节标题是否存在
        titleIsExist(chapterReq);
        // 添加
        ChapterDO chapterDO = new ChapterDO();
        BeanUtils.copyProperties(chapterReq, chapterDO);
        return this.save(chapterDO);
    }

    @Override
    public Boolean customizeUpdate(ChapterReq chapterReq, Long id) {
        // 判断该培训机构下的章节标题是否存在
        titleIsExist(chapterReq);
        // 修改
        ChapterDO chapterDO = new ChapterDO();
        BeanUtils.copyProperties(chapterReq, chapterDO);
        return this.updateById(chapterDO);
    }

    @Override
    public Boolean customizeDelete(Long id) {
        // 查询下面是否有子章节
        LambdaQueryWrapper<ChapterDO> chapterDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chapterDOLambdaQueryWrapper.eq(ChapterDO::getParentId, id);
        List<ChapterDO> chapterDOList = this.list(chapterDOLambdaQueryWrapper);
        ValidationUtils.throwIf(!ObjectUtils.isEmpty(chapterDOList), "该章节下有子章节无法删除");

        // 查询下面是否有视频
        LambdaQueryWrapper<VideoDO> videoDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        videoDOLambdaQueryWrapper.eq(VideoDO::getChapterId, id);
        List<VideoDO> videoDOList = videoMapper.selectList(videoDOLambdaQueryWrapper);
        ValidationUtils.throwIf(!ObjectUtils.isEmpty(videoDOList), "该章节下有培训视频无法删除");

        this.delete(Arrays.asList(id));
        return true;
    }

    /**
     * 判断该培训机构下的章节标题是否存在
     * 
     * @param chapterReq
     */
    private void titleIsExist(ChapterReq chapterReq) {
        LambdaQueryWrapper<ChapterDO> chapterDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chapterDOLambdaQueryWrapper.eq(ChapterDO::getTrainingId, chapterReq.getTrainingId())
            .eq(ChapterDO::getTitle, chapterReq.getTitle());
        ChapterDO chapterDODB = this.getOne(chapterDOLambdaQueryWrapper);

        ValidationUtils.throwIf(!ObjectUtils.isEmpty(chapterDODB) && !chapterDODB.getId()
            .equals(chapterReq.getId()), "该培训内容下已有该章节");

    }
}