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

package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）查询条件
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
@Data
@Schema(description = "机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）查询条件")
public class OrgTrainingPriceQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 八大类项目ID（关联八大类字典表主键）
     */
    @Schema(description = "八大类项目ID（关联八大类字典表主键）")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 八大类项目名称
     */
    @Schema(description = "八大类项目名称")
    private String projectName;

    /**
     * 机构ID（关联机构表主键）
     */
    @Schema(description = "机构ID（关联机构表主键）")
    @Query(type = QueryType.EQ)
    private Long orgId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String orgName;

    /**
     * 培训价格（元，精确到分，对应“价格表”核心需求）
     */
    @Schema(description = "培训价格（元，精确到分，对应“价格表”核心需求）")
    @Query(type = QueryType.EQ)
    private BigDecimal price;
}