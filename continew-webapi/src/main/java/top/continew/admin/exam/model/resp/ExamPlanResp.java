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
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;
import java.util.List;

/**
 * 考试计划信息
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@Schema(description = "考试计划信息")
public class

ExamPlanResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private Long examProjectId;
    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    private String examPlanName;
    /**
     * 计划年份
     */
    @Schema(description = "计划年份")
    private String planYear;

    @Schema(description = "审核状态字符串")
    @ExcelProperty(value = "审核状态字符串")
    private String statusString;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间")
    private LocalDateTime endTime;

    /**
     * 考试地点id
     */
    @Schema(description = "考试地点id")
    private Long locationId;

    /**
     * 最大考生人数
     */
    @Schema(description = "最大考生人数")
    private Integer maxCandidates;

    /**
     * 实际考生人数
     */
    @Schema(description = "实际考生人数")
    private Integer actualCandidates;

    /**
     * 考试地点名称
     */
    @Schema(description = "考试地点名称")
    @ExcelProperty(value = "考试地点名称")
    private String locationName;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 机构类型，0作业人员，1检验人员
     */
    @Schema(description = "机构类型，0作业人员，1检验人员")
    private Integer planType;

    /**
     * 监考员分配类型（1：第一次随机分配 2：第二次随机分配 3：管理员指派）
     */
    @Schema(description = "监考员分配类型（1：第一次随机分配 2：第二次随机分配 3：管理员指派）")
    private Integer assignType;

    /**
     * 计划状态
     */
    @Schema(description = "计划状态")
    private Integer status;

    /**
     * 确定最终考试时间以及地点状态
     */
    @Schema(description = "确定最终考试时间以及地点状态")
    private Integer isFinalConfirmed;

    /**
     * 审批人ID列表
     */
    @Schema(description = "审批人")
    private String approvedUser;

    /**
     * 审批时间列表
     */
    @Schema(description = "审批时间列表")
    private String approvalTime;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String redeme;
    /**
     * 报名开始时间
     */
    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;
    /**
     * 报名结束时间
     */
    @Schema(description = "报名结束时间")
    private LocalDateTime enrollEndTime;
    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间戳
     */
    @Schema(description = "更新时间戳")
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Boolean isDeleted;

    /**
     * 展示图
     */
    @Schema(description = "展示图")
    private String imageUrl;
    /**
     * 监考人员名单
     */
    @Schema(description = "监考人员名单")
    private List<String> invigilatorList;
    /**
     * 考试费用
     */
    @Schema(description = "考试费用")
    private Long examFee;
}