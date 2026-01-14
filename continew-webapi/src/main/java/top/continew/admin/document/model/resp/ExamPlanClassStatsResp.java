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

package top.continew.admin.document.model.resp;

import lombok.Data;

/**
 * 考试计划班级统计 VO
 * 
 * 统计每个考试计划下各班级的报名人数、考试人数、及格人数、成绩录入情况和证书生成情况
 */
@Data
public class ExamPlanClassStatsResp {

    /** 考试计划ID */
    private Long planId;

    /** 考试计划名称 */
    private String examPlanName;

    /** 项目名称 */
    private String projectName;

    /** 机构名称 */
    private String orgName;

    /** 班级ID */
    private Long classId;

    /** 班级名称 */
    private String className;

    /** 报名人数 */
    private Integer enrollCount;

    /** 实际参加考试人数 */
    private Integer examCount;

    /** 及格人数 */
    private Integer passedCount;

    /** 不及格人数 */
    private Integer failedCount;

    /** 未录入成绩人数 */
    private Integer notEnteredCount;

    /** 已生成证书人数 */
    private Integer certificateGeneratedCount;

    /** 未生成证书人数 */
    private Integer certificateNotGeneratedCount;
}
