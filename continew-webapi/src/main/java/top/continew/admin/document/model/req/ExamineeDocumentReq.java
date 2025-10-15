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

package top.continew.admin.document.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生资料关系参数
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Data
@Schema(description = "创建或修改考生资料关系参数")
public class ExamineeDocumentReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long examineeId;

    /**
     * 资料ID
     */
    @Schema(description = "资料ID")
    @NotNull(message = "资料ID不能为空")
    private Long documentId;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    @NotNull(message = "创建人ID不能为空")
    private Long createUser;
}