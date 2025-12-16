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

package top.continew.admin.invigilate.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 监考员资质证明响应 DTO
 *
 * @author
 * @since 2025/12/03
 */
@Data
@Schema(description = "监考员资质证明响应 DTO")
public class UserQualificationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "资质ID")
    private Long id;

    @Schema(description = "类别名称（来自 ted_category.name）")
    private String categoryName;

    @Schema(description = "资质证明 URL")
    private String qualificationUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
