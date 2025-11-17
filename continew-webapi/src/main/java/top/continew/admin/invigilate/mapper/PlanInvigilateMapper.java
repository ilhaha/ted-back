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

package top.continew.admin.invigilate.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.continew.admin.invigilate.model.entity.Grades;
import top.continew.admin.invigilate.model.resp.InvigilatorAssignResp;
import top.continew.admin.invigilate.model.resp.InvigilatorPlanResp;
import top.continew.admin.invigilate.model.resp.InvigilateExamDetailResp;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.invigilate.model.entity.PlanInvigilateDO;

import java.util.List;

/**
 * 考试计划监考人员关联 Mapper
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
@Mapper
public interface PlanInvigilateMapper extends BaseMapper<PlanInvigilateDO> {

    /**
     * 根据监考人员Id和监考状态分页查询数据
     *
     * @param invigilatorId
     * @param invigilateStatus
     * @return
     */
    @Select("select tep.id,tep.exam_plan_name as planName,tep.start_time,tep.end_time,tep.image_url from ted_plan_invigilate tpi " + "inner join ted_exam_plan tep on tpi.exam_plan_id = tep.id " + "where tpi.invigilator_id = #{invigilatorId} and tpi.invigilate_status = #{invigilateStatus} and tep.is_deleted=0 " + "order by tep.start_time desc " + "limit #{pageSize} offset #{offset}")
    List<InvigilatorPlanResp> queryEnrollRespByInvigilatorIdAndInvigilateStatus(Long invigilatorId,
                                                                                Integer invigilateStatus,
                                                                                int pageSize,
                                                                                int offset);

    /**
     * 查询总条数
     *
     * @param invigilatorId
     * @param invigilateStatus
     * @return
     */
    @Select("select count(*) from ted_plan_invigilate tpi " + "inner join ted_exam_plan tep on tpi.exam_plan_id = tep.id " + "where tpi.invigilator_id = #{invigilatorId} and tpi.invigilate_status = #{invigilateStatus} ")
    Long queryTotal(Long invigilatorId, Integer invigilateStatus);

    /**
     * 获取监考的考试详情
     *
     * @param invigilatorId
     * @param examId
     * @return
     */
    @Select("select tep.exam_plan_name,tep.image_url,tep.start_time,tep.end_time,tep.redeme,tel.location_name,tpi.invigilate_status from ted_plan_invigilate tpi " + "inner join ted_exam_plan tep on tep.id = tpi.exam_plan_id " + "inner join ted_exam_location tel on tep.location_id = tel.id " + "where tpi.invigilator_id = #{invigilatorId} and tpi.exam_plan_id = #{examId} and tep.is_deleted = 0")
    InvigilateExamDetailResp queryExamDetail(Long invigilatorId, Long examId);

    /**
     * 更新监考状态 根据examPlanId
     *
     * @param examPlanId
     * @param invigilateStatus
     */
    @Update("update ted_plan_invigilate " + "set invigilate_status = #{invigilateStatus} " + "where exam_plan_id = #{examPlanId}")
    void updateInvigilateStatus(Long examPlanId, Long invigilateStatus);

    /**
     * 批量录入考试成绩
     *
     * @param list
     */
    void batchInsertOrUpdateGrades(List<Grades> list);

    /**
     * 判断是否为对应监考状态
     *
     * @param examPlanId
     * @param invigilateStatus
     * @return
     */
    @Select("select count(*) from ted_plan_invigilate " + "where exam_plan_id = #{examPlanId} and invigilate_status = #{invigilateStatus}")
    Long isInvigilateStatus(Long examPlanId, Long invigilateStatus);

    /**
     * 查询实际参加考试的人数
     *
     * @param examId
     * @return
     */
    @Select("select sum(enrolled_count) from ted_plan_classroom where plan_id = #{examPlanId} ")
    Long queryHowMuchCandidates(Long examId);

    /**
     * 查询已经录入了多少条考试记录(被拒绝的不算 需要重新录入）
     *
     * @param examId
     * @return
     */
    @Select("select count(*) from ted_exam_records where plan_id = #{examId} and exam_scores is not null and review_status != 2 ")
    Long queryHowMuchGradesRecords(Long examId);

    /**
     * 查询已经审核了多少考试记录
     *
     * @param examId
     * @return
     */
    @Select("select count(*) from ted_exam_records where plan_id = #{examId} and review_status = 1")
    Long queryHowMuchGradesReview(Long examId);

    /**
     * 获取所有待录入和已录入和被拒绝的考试记录
     *
     * @param examId
     * @return
     */
    @Select("select plan_id,candidate_id,exam_scores,answer_sheet_url,review_status,nickname from ted_exam_records " + "left join sys_user su on ted_exam_records.candidate_id = su.id " + "where plan_id = #{examId} ")
    List<Grades> queryAlreadyCommitOrReject(Long examId);

    /**
     * 查询需要审核的纪律
     *
     * @param examId
     * @return
     */
    @Select("select plan_id,candidate_id,exam_scores,answer_sheet_url,review_status,su.nickname from ted_exam_records " + "left join sys_user su on ted_exam_records.candidate_id = su.id " + "where plan_id = #{examId}")
    List<Grades> queryNeedReviewByExamId(Long examId);

    /**
     * 查询审核被拒绝的考生成绩数量
     *
     * @param examId
     * @return
     */
    @Select("select count(*) from ted_exam_records " + "where plan_id = #{examId} and review_status = 2 and is_deleted = 0")
    Long queryHowMuchReviewReject(Long examId);

    @Select("select * from ted_plan_invigilate where  exam_plan_id= #{examId} and invigilator_id= #{userId}")
    PlanInvigilateDO selectByUserId(Long userId, Long examId);

    @Select("select invigilator_id from ted_plan_invigilate where  exam_plan_id= #{examId} AND classroom_id=#{classRoomId}")
    List<Long> selectByExamId(Long examId, Long classRoomId);

    @Select("update ted_plan_invigilate set exam_password=#{captcha} ${ew.customSqlSegment}   ")
    void deductBalanceByIds(String captcha, @Param(Constants.WRAPPER) QueryWrapper<PlanInvigilateDO> wrapper);

    /**
     * 根据计划id获取计划分配的监考员信息
     * @param planId
     * @return
     */
    List<InvigilatorAssignResp> getListByPlanId(@Param("planId") Long planId);
}
