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

package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.exam.mapper.LocationClassroomMapper;
import top.continew.admin.exam.model.entity.LocationClassroomDO;
import top.continew.admin.exam.model.resp.*;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ClassroomMapper;
import top.continew.admin.exam.model.entity.ClassroomDO;
import top.continew.admin.exam.model.query.ClassroomQuery;
import top.continew.admin.exam.model.req.ClassroomReq;
import top.continew.admin.exam.service.ClassroomService;

import java.util.List;

/**
 * 考场业务实现
 *
 * @author Anton
 * @since 2025/05/14 16:34
 */
@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl extends BaseServiceImpl<ClassroomMapper, ClassroomDO, ClassroomResp, ClassroomDetailResp, ClassroomQuery, ClassroomReq> implements ClassroomService {

    private final LocationClassroomMapper locationClassroomMapper;

    private final ClassroomMapper classroomMapper;

    @Override
    public PageResp<ClassroomResp> page(ClassroomQuery query, PageQuery pageQuery) {
        QueryWrapper<ClassroomDO> wrapper = this.buildQueryWrapper(query);
        wrapper.eq("tc.is_deleted", 0);
        super.sort(wrapper, pageQuery);
        IPage<ClassroomResp> page = baseMapper.selectExamLocation(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), wrapper);
        PageResp<ClassroomResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    @Override
    public List<ExamLocationResp> getExamLocation() {
        return baseMapper.getExamLocation();
    }

    @Override
    public List<ClassroomResp> getClassroomList(Long planId) {
        return classroomMapper.getClassroomList(planId);
    }

    @Override
    public ClassroomDetailResp get(Long id) {
        return baseMapper.getClassroomDeteil(id);
    }

    @Override
    public Long add(ClassroomReq req) {
        //1.插入考试教师表
        Long classroomId = super.add(req);
        //2.还需插入考场地点和考试教室表
        LocationClassroomDO locationClassroomDO = new LocationClassroomDO();
        locationClassroomDO.setClassroomId(Math.toIntExact(classroomId));
        locationClassroomDO.setLocationId(Math.toIntExact(req.getExamLocationId()));
        locationClassroomMapper.insert(locationClassroomDO);
        return classroomId;
    }

    @Override
    public void delete(List<Long> ids) {
        super.delete(ids);
        //构建条件删除关联表
        QueryWrapper<LocationClassroomDO> wrapper = new QueryWrapper<>();
        wrapper.in("classroom_id", ids);
        locationClassroomMapper.delete(wrapper);
    }

    @Override
    public void update(ClassroomReq req, Long id) {
        //1.更新表
        super.update(req, id);
        //2.更新地点与考场关联表
        Long examLocationId = req.getExamLocationId();
        Long classroomId = req.getId();
        UpdateWrapper<LocationClassroomDO> locationClassroomDOUpdateWrapper = new UpdateWrapper<>();
        locationClassroomDOUpdateWrapper.set("location_id", examLocationId).eq("classroom_id", classroomId);
        locationClassroomMapper.update(locationClassroomDOUpdateWrapper);
    }
}