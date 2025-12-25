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

package top.continew.admin.exam.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ilhaha
 * @Create 2025/10/16 15:31
 *
 *         机构获取符合自身八大类的考试计划VO
 */
@Data
public class OrgExamPlanVO {

    /**
     * 考试计划id
     */
    private Long examPlanId;

    /**
     * 考试计划名称
     */
    private String examPlanName;
    /**
     * 项目ID
     */
    private Long examProjectId;

    /**
     * 考试项目id
     */
    private Long projectId;

    /**
     * 考试项目
     */
    private String projectName;

    /**
     * 项目代号
     */
    private String projectCode;

    /**
     * 考试时长(分钟)
     */
    private Integer examDuration;

    /**
     * 八大类
     */
    private String categoryName;

    /**
     * 计划年份
     */
    private String planYear;

    /**
     * 考试开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 考试地点id
     */
    private Long locationId;

    /**
     * 最大考生人数
     */
    private Integer maxCandidates;

    /**
     * 实际考生人数
     */
    private Integer actualCandidates;

    /**
     * 考试人员类型
     */
    private Integer planType;

    /**
     * 计划状态
     */
    private Integer status;

    /**
     * 报名开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime enrollStartTime;
    /**
     * 报名结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime enrollEndTime;
    /**
     * 描述
     */
    private String redeme;
    /**
     * 考试费用
     */
    private String examFee;

    /**
     * 考试费用
     */
    private String projectExamFee;

    /**
     * 剩余报名人数
     */
    private Integer remainingSlots;

    /**
     * 最终确认时间地点（0：待管理员确定 1：待中心主任确定 2：中心主任确定 3：中心主任驳回）
     */
    private Integer isFinalConfirmed;

}
