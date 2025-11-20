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

package top.continew.admin.worker.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改作业人员准考证参数
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Data
@Schema(description = "创建或修改作业人员准考证参数")
public class WorkerExamTicketReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    @NotNull(message = "报名id不能为空")
    private Long enrollId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @NotBlank(message = "作业人员姓名不能为空")
    @Length(max = 255, message = "作业人员姓名长度不能超过 {max} 个字符")
    private String candidateName;

    /**
     * 准考证地址
     */
    @Schema(description = "准考证地址")
    @NotBlank(message = "准考证地址不能为空")
    @Length(max = 255, message = "准考证地址长度不能超过 {max} 个字符")
    private String ticketUrl;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @NotNull(message = "创建时间不能为空")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @NotNull(message = "更新时间不能为空")
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @NotNull(message = "删除标记(0未删,1已删)不能为空")
    private Integer isDeleted;
}