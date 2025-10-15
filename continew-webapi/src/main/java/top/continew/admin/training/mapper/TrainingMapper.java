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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.training.model.resp.TrainingResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.TrainingDO;

import java.util.List;

/**
 * 培训主表 Mapper
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Mapper
public interface TrainingMapper extends BaseMapper<TrainingDO> {
    IPage<TrainingResp> selectCurrentKSPage(@Param("page") Page<TrainingDO> objectPage,
                                            @Param(Constants.WRAPPER) QueryWrapper<TrainingDO> queryWrapper,
                                            @Param("userId") Long userId);

    IPage<TrainingResp> gettrainingList(@Param("page") Page<TrainingDO> page,
                                        @Param(Constants.WRAPPER) QueryWrapper<TrainingDO> queryWrapper);

    /**
     * 根据专家Id查询专家是否存在
     * 
     * @param expertId
     * @return
     */
    @Select("select count(*) from ted_expert where id = #{expertId}")
    int isExistenceExpert(Long expertId);

    IPage<TrainingResp> queryTraningInPage(@Param("page") Page<TrainingResp> page,
                                           @Param(Constants.WRAPPER) Wrapper<TrainingResp> wrapper);

    /**
     * 查询用户已报名的的考试计划对应的培训计划 (全部）
     * 
     * @param userId
     * @return
     */
    @Select("select tpt.training_id " + "from ted_project_training tpt inner join ted_training tt on tpt.training_id = tt.id and tt.status = 1 and tt.is_deleted = 0 " + "where tpt.project_id in (" + "select tep.exam_project_id " + "from ted_exam_plan tep " + "where id in (" + "select te.exam_plan_id from ted_enroll te where user_id = #{userId} and te.enroll_status = 1 " + ")) and tpt.is_deleted = 0 ")
    List<Long> selectStudentNeedTraing(Long userId);

    /**
     * 查询用户已报名的的考试计划对应的培训计划 (根据培训状态查询）
     * 
     * @param userId
     * @return
     */
    @Select("select tpt.training_id " + "from ted_project_training tpt " + "inner join ted_training tt on tpt.training_id = tt.id and tt.status = 1 and tt.is_deleted = 0 " + "inner join ted_student_training tst on tpt.training_id = tst.training_id and tst.student_id = #{userId} and tst.status = #{status} and tst.is_deleted = 0 " + "where tpt.project_id in (" + "select tep.exam_project_id " + "from ted_exam_plan tep " + "where id in (" + "select te.exam_plan_id from ted_enroll te where user_id = #{userId} and te.enroll_status = 1 " + ")) and tpt.is_deleted = 0 ")
    List<Long> selectStudentNeedTraing1(Long userId, Long status);
}
