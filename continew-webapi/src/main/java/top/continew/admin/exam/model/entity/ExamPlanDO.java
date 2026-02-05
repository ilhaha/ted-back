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

package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 考试计划实体
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@TableName("ted_exam_plan")
public class ExamPlanDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 考试计划名称
     */
    private String examPlanName;
    /**
     * 项目ID
     */
    private Long examProjectId;

    /**
     * 计划年份
     */
    private String planYear;

    /**
     * 考试开始时间
     */
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
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
     * 机构类型，0作业人员，1检验人员
     */
    private Integer planType;

    /**
     * 计划状态
     */
    private Integer status;

    /**
     * 最终确认时间地点（0：待管理员确定 1：待中心主任确定 2：中心主任确定 3：中心主任驳回）
     */
    private Integer isFinalConfirmed;

    /**
     * 监考员分配类型（1：第一次随机分配 2：第二次随机分配 3：管理员指派）
     */
    private Integer assignType;

    /**
     * 审批人ID列表
     */
    private String approvedUsers;

    /**
     * 审批时间列表
     */
    private String approvalTime;
    /**
     * 报名开始时间
     */
    private LocalDateTime enrollStartTime;
    /**
     * 报名结束时间
     */
    private LocalDateTime enrollEndTime;

    /**
     * 准考证下载截止时间
     */
    private LocalDateTime admitCardEndTime;

    /**
     * 描述
     */
    private String redeme;

    /**
     * 删除标记
     */
    private Boolean isDeleted;

    /**
     * 展示图
     */
    private String imageUrl;
    /**
     * 考试费用
     */
    private String examFee;

}