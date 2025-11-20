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

import org.apache.ibatis.annotations.Param;
import top.continew.admin.exam.model.entity.ExamPlanClassroomDO;
import top.continew.starter.data.mp.base.BaseMapper;

import java.util.List;

/**
 * 考试计划考场关联表 Mapper
 *
 */
public interface ExamPlanClassroomMapper extends BaseMapper<ExamPlanClassroomDO> {

    /**
     * 通过考场ID查询所有关联的考试计划id
     * 
     * @param classroomId 考场ID
     * @return 考试计划与考场关联信息列表
     */
    List<ExamPlanClassroomDO> selectByClassroomId(@Param("classroomId") Long classroomId);
}
