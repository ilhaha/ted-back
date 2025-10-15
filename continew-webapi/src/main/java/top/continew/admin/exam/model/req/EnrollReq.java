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
 * 创建或修改考生报名表参数
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Data
@Schema(description = "创建或修改考生报名表参数")
public class EnrollReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试状态（0：未报名，1：已报名：2：已完成，3：已过期）
     */
    @Schema(description = "考试状态（0：未报名，1：已报名：2：已完成，3：已过期）")
    @NotNull(message = "考试状态（0：未报名，1：已报名：2：已完成，3：已过期）不能为空")
    private Long enrollStatus;

    /**
     * 考试计划id
     */
    @Schema(description = "考试计划id")
    @NotNull(message = "考试计划id")
    private Long examPlanId;

}