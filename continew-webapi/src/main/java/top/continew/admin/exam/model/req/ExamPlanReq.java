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

package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.util.List;

/**
 * 创建或修改考试计划参数
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@Schema(description = "创建或修改考试计划参数")
public class ExamPlanReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Schema(description = "项目id")
    @NotNull(message = "项目不能为空")
    private Long examProjectId;

    /**
     * 考试计划名称
     */
    @Schema(description = "计划名称")
    @NotBlank(message = "计划名称不能为空")
    @Size(min = 2, max = 50, message = "计划名称长度必须在2-50个字符之间")
    private String examPlanName;

    /**
     * 考试开始时间范围
     */
    @Schema(description = "考试开始时间范围")
    @Size(min = 2, max = 2, message = "考试开始时间范围必须包含 2 个时间点")
    private List<@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "考试开始时间格式不正确") String> dateRange;

    @Schema(description = "报名开始时间范围")
    @Size(min = 2, max = 2, message = "报名开始时间范围必须包含 2 个时间点")
    private List<@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "报名开始时间格式不正确") String> enrollList;

    /**
     * 考试地点id
     */
    @Schema(description = "考试地点")
    private Long locationId;


    /**
     * 考场id列表
     */
    @Schema(description = "考场ID列表")
    @NotNull(message = "考场ID列表不能为空")
    private List<Long> classroomId;

    @Schema(description = "计划考试人数")
    @NotNull(message = "计划人数不能为空")
    @Min(value = 1, message = "计划人数不能小于1")
    private Integer maxCandidates;

    @Schema(description = "展示图")
    private String imageUrl;

    private LocalDateTime enrollEndTime;
    private LocalDateTime enrollStartTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}