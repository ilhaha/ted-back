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
import top.continew.admin.exam.model.dto.ExamPlanDTO;
import top.continew.admin.exam.model.entity.UserNamesDO;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.vo.InvigilateExamPlanVO;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ExamPlanDO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 考试计划 Mapper
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
public interface ExamPlanMapper extends BaseMapper<ExamPlanDO> {

    IPage<ExamPlanDetailResp> selectExamPlanPage(@Param("page") Page<Object> objectPage,
                                                 @Param(Constants.WRAPPER) QueryWrapper<ExamPlanDO> queryWrapper);

    ExamPlanDetailResp selectDetailById(Long id);

    // 查询所有考试计划列表
    List<ExamPlanDO> selectAllList();

    List<Long> selectAllProjectIds(List<Long> examPlanIds);

    @Select("SELECT from")
    List<String> selectInvigilatorList(@Param("examPlanId") Long examPlanId);

    void savePlanClassroom(@Param("planId") Long planId, @Param("classroomId") List<Long> classroomId);

    List<Long> getPlanExamClassroom(@Param("planId") Long planId);

    void deletePLanExamClassroom(@Param("planId") Long planId);

    @Select("select classroom_id from ted_plan_classroom where plan_id = #{examPlanId}")
    Integer selectClassroomById(@Param("examPlanId") Integer examPlanId);

    @Select("select id value,exam_plan_name label from ted.ted_exam_plan")
    List<ProjectVo> getSelectOptions();

    @Select("select id,nickname from sys_user")
    List<UserNamesDO> selectUserNames();

    Integer hasClassroomTimeConflict(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("classroomId") List<Long> classroomId,
                                     @Param("planId") Long planId);

    List<Long> getPlanLocationIdsById(@Param("planIds") List<Long> planIds);

    IPage<OrgExamPlanVO> orgGetPlanList(@Param("page") Page<Object> objectPage,
                                        @Param(Constants.WRAPPER) QueryWrapper<ExamPlanDO> queryWrapper,
                                        @Param("userId") Long userId);

    /**
     * 批量更新最大容纳人数
     */
    void batchUpdatePlanMaxCandidates(@Param("planList") List<ExamPlanDTO> planList);

    List<String> listConflictClassrooms(@Param("startTime") LocalDateTime startTime,
                                        @Param("classroomIds") List<Long> classroomIds);

    /**
     * 获取计划分配的所有理论考场ID
     * 
     * @param planId
     * @return
     */
    List<Long> getPlanExamTheoryClassroom(@Param("planId") Long planId);

    IPage<InvigilateExamPlanVO> invigilateGetPlanList(@Param("page") Page<Object> objectPage,
                                                      @Param(Constants.WRAPPER) QueryWrapper<ExamPlanDO> queryWrapper,
                                                      @Param("userId") Long userId);

    List<Map<String, Object>> findExamPlansByUsername(@Param("username") String username);

    List<Map<String, Object>> selectListByPlanType(@Param("planType") Integer planType);
}