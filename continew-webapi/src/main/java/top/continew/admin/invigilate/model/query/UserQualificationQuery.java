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

package top.continew.admin.invigilate.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 监考员资质证明查询条件
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Data
@Schema(description = "监考员资质证明查询条件")
public class UserQualificationQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @Query(type = QueryType.EQ)
    private Long userId;

    /**
     * 八大类ID
     */
    @Schema(description = "八大类ID")
    @Query(type = QueryType.EQ)
    private Long categoryId;

    /**
     * 资质证明URL
     */
    @Schema(description = "资质证明URL")
    @Query(type = QueryType.EQ)
    private String qualificationUrl;
}