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
import java.util.List;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间")
    @ExcelProperty(value = "考试结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 考试地点id
     */
    @Schema(description = "考试地点id")
    @ExcelProperty(value = "考试地点id")
    private Long locationId;

    /**
     * 考试时长
     */
    @Schema(description = "考试时长")
    @ExcelProperty(value = "考试时长")
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
     * 监考员分配类型（1：第一次随机分配 2：第二次随机分配 3：管理员指派）
     */
    @Schema(description = "监考员分配类型（1：第一次随机分配 2：第二次随机分配 3：管理员指派）")
    @ExcelProperty(value = "监考员分配类型（1：第一次随机分配 2：第二次随机分配 3：管理员指派）")
    private Integer assignType;

    /**
     * 机构类型，0作业人员，1检验人员
     */
    @Schema(description = "机构类型，0作业人员，1检验人员")
    @ExcelProperty(value = "机构类型，0作业人员，1检验人员")
    private Integer planType;

    /**
     * 确定最终考试时间以及地点状态
     */
    @Schema(description = "确定最终考试时间以及地点状态")
    @ExcelProperty(value = "确定最终考试时间以及地点状态")
    private Integer isFinalConfirmed;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime enrollStartTime;

    /**
     * 报名结束时间
     */
    @Schema(description = "报名结束时间")
    @ExcelProperty(value = "报名结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime enrollEndTime;
    /**
     * 考试费用
     */
    @Schema(description = "考试费用")
    @ExcelProperty(value = "考试费用")
    private Long examFee;

    /**
     * 理论考场
     */
    @Schema(description = "理论考场")
    @ExcelProperty(value = "理论考场")
    private List<Long> theoryClassroomId;
    /**
     * 实操考场
     */
    @Schema(description = "实操考场")
    @ExcelProperty(value = "实操考场")
    private List<Long> operationClassroomId;

    /**
     * 已报名人数
     */
    @Schema(description = "已报名人数")
    @ExcelProperty(value = "已报名人数")
    private Integer enrolledCount;

    /**
     * 开考密码
     */
    @Schema(description = "开考密码")
    @ExcelProperty(value = "开考密码")
    private String examPassword;

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

    /**
     * 缺考人数
     */
    private Integer absentCount;

    /**
     * 实操考试（1有，0没有）
     */
    private Integer hasOper;

    /**
     * 理论考试（1有，0没有）
     */
    private Integer hasTheory;

    /**
     * 道路考试（1有，0没有）
     */
    private Integer hasRoad;


    /**
     * 准考证下载截止时间
     */
    private LocalDateTime admitCardEndTime;

}