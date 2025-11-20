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

package top.continew.admin.document.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 机构报考-考生扫码上传文件查询条件
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "机构报考-考生扫码上传文件查询条件")
public class EnrollPreUploadQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @Query(type = QueryType.LIKE, columns = "su.nickname")
    private Long candidatesName;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @Query(type = QueryType.LIKE, columns = "ep.exam_plan_name")
    private String planName;

    /**
     * 机构id
     */
    @Schema(description = "机构id")
    @Query(type = QueryType.EQ, columns = "org.id")
    private Long orgId;

    /**
     * 审核状态
     */
    @Schema(description = "审核状态")
    @Query(type = QueryType.EQ, columns = "tepu.status")
    private Integer status;
}