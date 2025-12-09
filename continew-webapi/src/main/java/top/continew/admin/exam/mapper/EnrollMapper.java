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
import org.apache.ibatis.annotations.Update;
import top.continew.admin.certificate.model.dto.CertificateInfoDTO;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.ExamCandidateVO;
import top.continew.admin.exam.model.vo.IdentityCardExamInfoVO;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.EnrollDO;

import java.util.List;
import java.util.Map;

/**
 * 考生报名表 Mapper
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
public interface EnrollMapper extends BaseMapper<EnrollDO> {
    //获取所有考试计划列表（与用户信息有关）
    public IPage<EnrollStatusResp> getEnrollList(@Param("page") Page<EnrollDO> page,
                                                 @Param(Constants.WRAPPER) QueryWrapper<EnrollDO> queryWrapper,
                                                 @Param("userId") Long userId);

    public EnrollDetailResp getAllDetailEnrollList(@Param("examPlanId") Long examPlanId, @Param("userId") Long userId);

    public EnrollStatusDetailResp getDetailEnroll(@Param("examPlanId") Long examPlanId);

    public List<CertificateInfoDTO> getCertificateList(@Param("examPlanId") Long examPlanId);

    public List<String> getDocumentList(@Param("examPlanId") Long examPlanId);

    //获取考生基本信息
    public EnrollInfoResp getEnrollInfo(@Param("userId") Long userId);

    //获取考生提交的资料列表
    List<String> getStudentDocumentList(@Param("userId") Long userId);

    // 获取考试成绩
    Map<String, String> getScore(@Param("identity") String identity);

    //获取已报名考试计划列表
    List<EnrollResp> getEnrolledPlan(@Param("userId") Long userId);

    IdentityCardExamInfoVO viewIdentityCardInfo(Long examPlanId, Long userId);

    //分页功能
    IPage<EnrollResp> getEnrollPage(@Param("page") Page<EnrollDO> page,
                                    @Param(Constants.WRAPPER) QueryWrapper<EnrollDO> queryWrapper);

    IPage<ExamCandidateVO> getExamCandidates(@Param("page") Page<EnrollDO> page,
                                             @Param(Constants.WRAPPER) QueryWrapper<EnrollDO> queryWrapper);

    /**
     * 获取当前考试已有人数
     */
    @Select("select sum(enrolled_count) from ted_plan_classroom where plan_id = #{examPlanId}")
    Long getEnrollCount(@Param("examPlanId") Long examPlanId);

    /**
     * 取消报名
     */
    @Update("update ted.ted_special_certification_applicant set is_deleted = 1  where plan_id = #{examPlanId} and candidates_id = #{userId}")
    void deleteFromApplicant(@Param("examPlanId") Long examPlanId, @Param("userId") Long userId);

    @Update("UPDATE ted.ted_enroll SET enroll_status = 0, is_deleted = 1 WHERE exam_plan_id = #{examPlanId} AND user_id = #{userId}")
    void deleteFromEnroll(@Param("examPlanId") Long examPlanId, @Param("userId") Long userId);

    /**
     * 更新报名状态
     */
    @Update("UPDATE ted.ted_enroll SET enroll_status = #{status}, update_time = NOW() WHERE exam_plan_id = #{examPlanId} AND user_id = #{userId} AND is_deleted = 0")
    void updateEnrollStatus(@Param("examPlanId") Long examPlanId,
                            @Param("userId") Long userId,
                            @Param("status") Long status);

    /**
     * 根据考生id查询申报记录
     * 
     * @param userId 考生id
     */
    @Select("SELECT * FROM ted.ted_enroll WHERE user_id = #{userId} AND enroll_status NOT IN (0, 3) AND is_deleted = 0 LIMIT 1")
    EnrollDO getByCandidateId(@Param("userId") Long userId);

    IPage<EnrollResp> getWorkerApplyList(@Param("page") Page<EnrollDO> page,
                                         @Param(Constants.WRAPPER) QueryWrapper<EnrollDO> queryWrapper);

    List<WorkerAuditNoticeResp> selectAuditNoticeToClass(@Param("classId") Long classId, @Param("planId") Long planId);

    Long getPlanEnrollCount(@Param("examPlanId") Long examPlanId);
}