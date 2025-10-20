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
 * 创建或修改特种设备人员资格申请参数
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Data
@Schema(description = "创建或修改特种设备人员资格申请参数")
public class SpecialCertificationApplicantReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "考试计划ID")
    @NotNull(message = "考试计划ID不能为空")
    private Long planId;

    @Schema(description = "申请表电子资料URL")
    @NotBlank(message = "申请表文件URL不能为空")
    private String imageUrl;

    @Schema(description = "审核状态（0：未审核，1：审核通过，2：退回补正，3：虚假资料）")
    private Integer status;

    @Schema(description = "审核意见或退回原因（选填）")
    private String remark;
}