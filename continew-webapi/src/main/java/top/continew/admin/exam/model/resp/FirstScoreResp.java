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

package top.continew.admin.exam.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;
import java.util.List;

/**
 * 考试记录信息
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Data
@Schema(description = "考试记录信息")
public class FirstScoreResp {

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    private Long planId;

    /**
     * 计划名称（后加）
     */
    @Schema(description = "计划名称")
    private String planName;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidateId;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    private String candidateName;


    /**
     * 考试得分，(项目id_得分#项目id_得分#)
     */
    @Schema(description = "考试得分，(项目id_得分#项目id_得分#)")
    private Integer examScores;


    /**
     * 实操成绩
     */
    @Schema(description = "实操成绩")
    @ExcelProperty(value = "实操成绩")
    private Integer operScores;


    /**
     * 考试结果（0不及格，1及格,2未录入）
     */
    @Schema(description = " 考试结果（0不及格，1及格,2未录入）")
    @ExcelProperty(value = " 考试结果（0不及格，1及格,2未录入）")
    private Integer examResultStatus;


    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}