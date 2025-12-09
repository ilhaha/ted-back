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

package top.continew.admin.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.exam.model.resp.ClassroomDetailResp;
import top.continew.admin.exam.model.resp.ClassroomResp;
import top.continew.admin.exam.model.resp.ExamLocationResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ClassroomDO;

import java.util.List;

/**
 * 考场 Mapper
 *
 * @author Anton
 * @since 2025/05/14 16:34
 */
public interface ClassroomMapper extends BaseMapper<ClassroomDO> {

    /**
     * 关联表查询考试地点信息
     * 
     * @return 考试地点信息
     *
     */

    IPage<ClassroomResp> selectExamLocation(@Param("page") Page<Object> objectPage,

                                            @Param(Constants.WRAPPER) QueryWrapper<ClassroomDO> queryWrapper);

    @Select("select id,location_name from ted_exam_location where is_deleted = 0")
    List<ExamLocationResp> getExamLocation();

    Long getMaxCandidates(@Param("classroomIds") List<Long> classroomId);

    /**
     * 查询当前考场已有人数
     */
    @Select("select count(user_id) from ted_enroll where classroom_id = #{classroomId} and is_deleted = 0 and enroll_status = 1")
    long getEnrolledCount(@Param("classroomId") Long classroomId);

    //原子更新考场人数
    int incrementEnrolledCount(@Param("classroomId") Long classroomId, @Param("planId") Long planId);

    //获取当前座位号
    @Select("select enrolled_count from ted_plan_classroom where classroom_id = #{classroomId} and plan_id = #{planId}")
    int getSeatNumber(@Param("classroomId") Long classroomId, @Param("planId") Long planId);

    @Select("select * from ted_classroom as c " + "join ted_plan_classroom as pc " + "on c.id = pc.classroom_id " + "where pc.plan_id = #{ planId }")
    List<ClassroomResp> getClassroomList(Long planId);

    /**
     * 查询当前计划已有人数
     */
    @Select("select count(user_id) from ted_enroll where exam_plan_id = #{planId} and is_deleted = 0 and enroll_status = 1")
    long getPlanCount(Long planId);

    ClassroomDetailResp getClassroomDeteil(Long id);
}