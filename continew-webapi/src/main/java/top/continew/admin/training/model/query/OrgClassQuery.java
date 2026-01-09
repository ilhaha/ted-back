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
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 培训机构班级查询条件
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@Schema(description = "培训机构班级查询条件")
public class OrgClassQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    @Query(type = QueryType.LIKE)
    private String className;

    /**
     * 班级id
     */
    @Schema(description = "班级id")
    @Query(type = QueryType.EQ,columns = "toc.id")
    private String classId;

    /**
     * 班级类型，0作业人员班级，1检验人员班级
     */
    @Schema(description = "班级类型，0作业人员班级，1检验人员班级")
    @Query(type = QueryType.EQ)
    private Integer classType;

    /**
     * 是否是机构查询
     */
    @Schema(description = "是否是机构查询")
    @QueryIgnore
    private Boolean isOrgQuery;
}