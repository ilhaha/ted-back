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

/**
 * 创建或修改考试计划报考班级参数
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Data
@Schema(description = "创建或修改考试计划报考班级参数")
public class PlanApplyClassReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @NotNull(message = "考试计划ID不能为空")
    private Long planId;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /**
     * 是否已确认成绩（0：未确认 1：已确认）
     */
    @Schema(description = "是否已确认成绩（0：未确认 1：已确认）")
    @NotNull(message = "是否已确认成绩（0：未确认 1：已确认）不能为空")
    private Boolean isScoreConfirmed;
}