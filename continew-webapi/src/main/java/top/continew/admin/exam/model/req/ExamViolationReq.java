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

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考试劳务费配置参数
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@Schema(description = "创建或修改考试劳务费配置参数")
public class ExamViolationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @NotNull(message = "计划ID不能为空")
    private Long planId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long candidateId;

    /**
     * 违规描述
     */
    @Schema(description = "违规描述")
    @NotBlank(message = "违规描述不能为空")
    @Length(max = 500, message = "违规描述长度不能超过 {max} 个字符")
    private String violationDesc;
}