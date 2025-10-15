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

import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamPlanVO {

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
     * 计划状态
     */
    private Integer status;

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
    ;
    private LocalDateTime enrollEndTime;
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
     * 考场列表
     */
    private List<Long> classroomList;
}
