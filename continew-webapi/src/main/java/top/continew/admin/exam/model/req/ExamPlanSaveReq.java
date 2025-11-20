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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author ilhaha
 * @Create 2025/3/14 16:37
 * @Version 1.0
 */
@Data
@Schema(description = "发布考试计划参数")
public class ExamPlanSaveReq {

    /**
     * 开始时间和结束时间
     */
    //    @Schema(description = "开始时间和结束时间")
    //    @Size(min = 2, max = 2, message = "时间范围必须包含且仅包含开始时间和结束时间")
    //    private List<String> dateRange;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    @NotBlank(message = "考试开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 开始时间和结束时间
     */
    @Schema(description = "开始时间和结束时间")
    private List<String> dateRange;

    /**
     * 开始时间和结束时间
     */
    @Schema(description = "报名开始时间和结束时间")
    @Size(min = 2, max = 2, message = "报名时间范围必须包含且仅包含开始时间和结束时间")
    private List<String> enrollList;

    /**
     * 计划名
     */
    @Schema(description = "计划名称")
    @NotBlank(message = "计划名称不能为空")
    @Size(min = 2, max = 50, message = "计划名称长度必须在2-50个字符之间")
    private String examPlanName;

    /**
     * 地点id
     */
    @Schema(description = "地点ID")
    @NotNull(message = "地点ID不能为空")
    @Positive(message = "地点ID必须是正整数")
    private Long locationId;

    /**
     * 机构类型，0作业人员，1检验人员
     */
    @Schema(description = "机构类型，0作业人员，1检验人员")
    private Integer planType;

    /**
     * 最大考生数
     */
    @Schema(description = "最大考生数")
    @NotNull(message = "最大考生数不能为空")
    @Min(value = 1, message = "最大考生数至少为1")
    private Integer maxCandidates;

    /**
     * 项目id
     */
    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须是正整数")
    private Long examProjectId;

    /**
     * 展示图
     */
    @Schema(description = "展示图URL")
    @NotBlank(message = "展示图URL不能为空")
    private String imageUrl;
    /**
     * 考场id列表
     */
    @Schema(description = "考场ID列表")
    @NotNull(message = "考场ID列表不能为空")
    private List<Long> classroomId;
    /**
     * 考试费用
     */
    @Schema(description = "考试费用")
    @NotNull(message = "考试费用不能为空")
    @Min(value = 0, message = "考试费用不能为负数")
    private Long examFee;
}
