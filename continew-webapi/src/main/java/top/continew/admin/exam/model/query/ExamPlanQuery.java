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

package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考试计划查询条件
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@Schema(description = "考试计划查询条件")
public class ExamPlanQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 考试计划名称
     */
    @Query(type = QueryType.LIKE, columns = "tep.exam_plan_name")
    private String examPlanName;

    /**
     * 项目名称
     */
    @Query(type = QueryType.LIKE, columns = "tp.project_name")
    private String projectName;

    /**
     * 地址名称
     */
    @Query(type = QueryType.LIKE, columns = "tel.location_name")
    private String locationName;

    /**
     * 计划年份
     */
    @Query(type = QueryType.EQ, columns = "tep.plan_year")
    private String planYear;

    /**
     * 计划id
     */
    @Query(type = QueryType.EQ, columns = "tep.id")
    private Integer planId;

    /**
     * 审批人
     */
    @Query(type = QueryType.LIKE, columns = "su.nickname")
    private String approvedUserString;

    /**
     * 考试人员类型
     */
    @Query(type = QueryType.EQ, columns = "tep.plan_type")
    private Integer planType;

    /**
     * 计划状态
     */
    @Query(type = QueryType.EQ, columns = "tep.status")
    private Integer status;

    /**
     * 监考状态
     */
//    @Query(type = QueryType.EQ, columns = "tpi.invigilate_status")
    @QueryIgnore
    private Integer invigilateStatus;

    /**
     * 班级名称
     */
    @Query(type = QueryType.LIKE, columns = "toc.class_name")
    private String className;

}