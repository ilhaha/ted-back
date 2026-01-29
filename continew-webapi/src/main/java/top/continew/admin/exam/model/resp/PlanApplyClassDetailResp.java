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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试计划报考班级详情信息
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试计划报考班级详情信息")
public class PlanApplyClassDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @ExcelProperty(value = "考试计划ID")
    private Long planId;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @ExcelProperty(value = "考试计划名称")
    private String examPlanName;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 考核项目种类名称
     */
    @Schema(description = "考核项目种类名称")
    @ExcelProperty(value = "考核项目种类名称")
    private String categoryName;

    /**
     * 总班级数
     */
    @Schema(description = "总班级数")
    @ExcelProperty(value = "总班级数")
    private Integer totalClassCount;

    /**
     * 已确认班级数
     */
    @Schema(description = "已确认班级数")
    @ExcelProperty(value = "已确认班级数")
    private Integer confirmedClassCount;

    /**
     * 未确认班级数
     */
    @Schema(description = "未确认班级数")
    @ExcelProperty(value = "未确认班级数")
    private Integer unconfirmedClassCount;

    /**
     * 考生总数（仅已确认班级）
     */
    @Schema(description = "考生总数（仅已确认班级）")
    @ExcelProperty(value = "考生总数（仅已确认班级）")
    private Integer totalCandidateCount;

    /**
     * 及格人数（仅已确认班级）
     */
    @Schema(description = "及格人数（仅已确认班级）")
    @ExcelProperty(value = "及格人数（仅已确认班级）")
    private Integer passCount;

    /**
     * 不及格人数（仅已确认班级）
     */
    @Schema(description = "不及格人数（仅已确认班级）")
    @ExcelProperty(value = "不及格人数（仅已确认班级）")
    private Integer failCount;
}