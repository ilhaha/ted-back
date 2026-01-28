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
import top.continew.admin.exam.model.dto.*;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.exam.model.entity.ExamRecordsDO;

import java.util.List;
import java.util.Map;

/**
 * 考试记录 Mapper
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
public interface ExamRecordsMapper extends BaseMapper<ExamRecordsDO> {
    IPage<ExamRecordDTO> getExamRecords(@Param("page") Page<ExamRecordsDO> page,
                                        @Param(Constants.WRAPPER) QueryWrapper<ExamRecordsDO> queryWrapper,
                                        @Param("roadExamTypeId") Long roadExamTypeId,
                                        @Param("metalProjectId") Long metalProjectId,
                                        @Param("nonmetalProjectId") Long nonmetalProjectId);

    ExamRecordsDO getRecordsById(Long id);

    ExamPresenceDTO hasOperationOrRoadExam(@Param("planId") Long planId, @Param("roadExamTypeId") Long roadExamTypeId);

    /**
     * 判断考试计划是否有某种考试
     * 
     * @param planIds
     * @return
     */
    List<CheckPlanHasExamTypeDTO> checkPlanHasExamType(@Param("planIds") List<Long> planIds,
                                                       @Param("roadExamTypeId") Long roadExamTypeId);

    /**
     * 获取证书信息
     * 
     * @param recordIds
     * @return
     */
    List<ExamRecordCertificateDTO> selectCertificateInfoByRecordIds(@Param("recordIds") List<Long> recordIds);

    /**
     * 根据考生id和计划id获取所在班级
     * 
     * @param pairs
     * @return
     */
    List<EnrollWithClassDTO> selectEnrollWithClass(@Param("pairs") List<UserPlanPairDTO> pairs);

    IPage<ExamRecordDTO> getCandidateExamRecordPage(@Param("page") Page<ExamRecordsDO> page,
                                                    @Param(Constants.WRAPPER) QueryWrapper<ExamRecordsDO> queryWrapper,
                                                    @Param("roadExamTypeId") Long roadExamTypeId);

    String selectWeldingProjectCodeByRecordId(@Param("recordId") Long recordId);

    /**
     * 批量查询考试记录对应焊接项目
     * @param recordIds 考试记录ID列表
     * @return Map<recordId, weldingProjectCodes>
     */
    List<Map<String, Object>> selectWeldingProjectCodeByRecordIds(@Param("recordIds") List<Long> recordIds);

    /**
     * 查询考试计划报考班级的考生考试情况
     * @param planId
     * @param roadExamTypeId
     * @param metalProjectId
     * @param nonmetalProjectId
     * @return
     */
    List<ExamRecordDTO> getClassExamTableList(@Param("planId") Long planId,
                                              @Param("roadExamTypeId") Long roadExamTypeId,
                                              @Param("metalProjectId") Long metalProjectId,
                                              @Param("nonmetalProjectId") Long nonmetalProjectId);
}