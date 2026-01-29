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

package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 机构申请焊接考试项目查询条件
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Data
@Schema(description = "机构申请焊接考试项目查询条件")
public class WeldingExamApplicationQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请机构ID
     */
    @Schema(description = "申请机构ID")
    @Query(type = QueryType.EQ, columns = "twea.org_id")
    private Long orgId;

    /**
     * 申请机构名称
     */
    @Schema(description = "申请机构名称")
    @Query(type = QueryType.LIKE, columns = "org.name")
    private String orgName;

    /**
     * 焊接类型：0-金属焊接，1-非金属焊接
     */
    @Schema(description = "焊接类型：0-金属焊接，1-非金属焊接")
    @Query(type = QueryType.EQ, columns = "twea.welding_type")
    private Integer weldingType;

    /**
     * 考试项目代码
     */
    @Schema(description = "考试项目代码")
    @Query(type = QueryType.LIKE, columns = "twea.project_code")
    private String projectCode;

    /**
     * 是否是机构查询
     */
    @Schema(description = "是否是机构查询")
    @QueryIgnore
    private Boolean isOrgQuery;

}