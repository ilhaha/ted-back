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

/**
 * 考试记录 Mapper
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
public interface ExamRecordsMapper extends BaseMapper<ExamRecordsDO> {
    IPage<ExamRecordDTO> getexamRecords(@Param("page") Page<ExamRecordsDO> page,
                                        @Param(Constants.WRAPPER) QueryWrapper<ExamRecordsDO> queryWrapper,
                                        @Param("roadExamTypeId") Long roadExamTypeId);

    ExamRecordsDO getRecordsById(Long id);

    ExamPresenceDTO hasOperationOrRoadExam(@Param("planId") Long planId, @Param("roadExamTypeId") Long roadExamTypeId);

    /**
     * 判断考试计划是否有某种考试
     * @param planIds
     * @return
     */
    List<CheckPlanHasExamTypeDTO> checkPlanHasExamType(@Param("planIds") List<Long> planIds,@Param("roadExamTypeId") Long roadExamTypeId);

    /**
     * 获取证书信息
     * @param recordIds
     * @return
     */
    List<ExamRecordCertificateDTO> selectCertificateInfoByRecordIds(@Param("recordIds") List<Long> recordIds);

    /**
     * 根据考生id和计划id获取所在班级
     * @param pairs
     * @return
     */
    List<EnrollWithClassDTO> selectEnrollWithClass(
            @Param("pairs") List<UserPlanPairDTO> pairs
    );

}