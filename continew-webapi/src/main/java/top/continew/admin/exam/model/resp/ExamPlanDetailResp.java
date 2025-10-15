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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试计划详情信息
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试计划详情信息")
public class ExamPlanDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    @ExcelProperty(value = "项目ID")
    private Long examProjectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @ExcelProperty(value = "项目名称")
    private String projectName;
    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @ExcelProperty(value = "考试计划名称")
    private String examPlanName;
    /**
     * 计划年份
     */
    @Schema(description = "计划年份")
    @ExcelProperty(value = "计划年份")
    private String planYear;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    @ExcelProperty(value = "考试开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间")
    @ExcelProperty(value = "考试结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 考试地点id
     */
    @Schema(description = "考试地点id")
    @ExcelProperty(value = "考试地点id")
    private Long locationId;

    /**
     * 考试市场
     */
    @Schema(description = "考试市场")
    @ExcelProperty(value = "考试市场")
    private Long examDuration;

    @Schema(description = "审核状态字符串")
    @ExcelProperty(value = "审核状态字符串")
    private String statusString;

    @Schema(description = "考试地点名称")
    @ExcelProperty(value = "考试地点名称")
    private String locationName;

    /**
     * 最大考生人数
     */
    @Schema(description = "最大考生人数")
    @ExcelProperty(value = "最大考生人数")
    private Integer maxCandidates;

    /**
     * 实际考生人数
     */
    @Schema(description = "实际考生人数")
    @ExcelProperty(value = "实际考生人数")
    private Integer actualCandidates;


    /**
     * 考试类型：0-理论考试，1-实操考试
     */
    @Schema(description = "考试类型：0-理论考试，1-实操考试")
    @ExcelProperty(value = "考试类型：0-理论考试，1-实操考试")
    private Integer examType;

    /**
     * 计划状态
     */
    @Schema(description = "计划状态")
    @ExcelProperty(value = "计划状态")
    private Integer status;

    /**
     * 审批人ID列表
     */
    @Schema(description = "审批人")
    @ExcelProperty(value = "审批人")
    private String approvedUser;

    /**
     * 审批时间列表
     */
    @Schema(description = "审批时间列表")
    @ExcelProperty(value = "审批时间列表")
    private String approvalTime;

    @Schema(description = "展示图")
    @ExcelProperty(value = "展示图")
    private String imageUrl;

    /**
     * 报名开始时间
     */
    @Schema(description = "报名开始时间")
    @ExcelProperty(value = "报名开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime enrollStartTime;

    /**
     * 报名结束时间
     */
    @Schema(description = "报名结束时间")
    @ExcelProperty(value = "报名结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime enrollEndTime;
    /**
     * 考试费用
     */
    @Schema(description = "考试费用")
    @ExcelProperty(value = "考试费用")
    private Long examFee;

    //    /**
    //     * 描述
    //     */
    //    @Schema(description = "描述")
    //    @ExcelProperty(value = "描述")
    //    private String redeme;
    //
    //    /**
    //     * 删除标记
    //     */
    //    @Schema(description = "删除标记")
    //    @ExcelProperty(value = "删除标记")
    //    private Boolean isDeleted;
}