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
import top.continew.admin.exam.model.resp.ExamLocationDetailResp;
import top.continew.admin.exam.model.vo.PlanLocationAndRoomVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ExamLocationDO;

import java.util.List;
import java.util.Map;

/**
 * 考试地点 Mapper
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
public interface ExamLocationMapper extends BaseMapper<ExamLocationDO> {
    //    Page<ExamLocationResp> getAllExamLocation(Page<ExamLocationResp> page, @Param("select") ExamLocationSelect select);

    IPage<ExamLocationDetailResp> selectExamLocationPage(@Param("page") Page<Object> objectPage,
                                                         @Param(Constants.WRAPPER) QueryWrapper<ExamLocationDO> queryWrapper);

    ExamLocationDetailResp getExamLocationDetail(@Param("id") Long id);

    /**
     * 通过项目id获取地址id、地址名称
     *
     * @param projectId
     * @return
     */
    //    @Select("SELECT el.id AS `value`, el.location_name AS `label` FROM ted_exam_location AS el " +
    //            "LEFT JOIN ted_proj_loc_assoc AS tpla ON tpla.location_id = el.id " +
    //            "WHERE tpla.project_id = #{projectId} " +
    //            "AND el.is_deleted = 0 " +
    //            "AND el.operational_status = 0")
    List<ProjectVo> getLocationSelect(@Param("projectId") Long projectId);

    List<ProjectVo> getClassRoomSelect(@Param("projectId") Long projectId);

    List<Map<String, Object>> selectClassroomList(@Param("projectId") Long projectId);

    /**
     * 根据计划id获取考试计划考试地点-考场详细信息
     * 
     * @param planId
     * @return
     */
    List<PlanLocationAndRoomVO> getPlanLocationAndRoom(@Param("planId") Long planId);
}
