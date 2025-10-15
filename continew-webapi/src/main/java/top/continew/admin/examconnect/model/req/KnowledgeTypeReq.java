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

package top.continew.admin.examconnect.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改知识类型，存储不同类型的知识占比参数
 *
 * @author Anton
 * @since 2025/04/07 10:39
 */
@Data
@Schema(description = "创建或修改知识类型，存储不同类型的知识占比参数")
public class KnowledgeTypeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 知识类型ID
     */
    @Schema(description = "知识类型ID")
    private Long id;
    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "知识类型名称")
    @NotNull(message = "知识类型名称")
    private String name;

    @Schema(description = "占比（百分比）")
    @NotNull(message = "占比（百分比）")
    @Min(value = 1, message = "占比（百分比）不能小于1")
    @Max(value = 100, message = "占比（百分比）不能大于100")
    private Integer proportion;
}