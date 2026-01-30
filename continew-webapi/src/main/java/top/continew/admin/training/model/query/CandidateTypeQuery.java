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
 * 考生类型查询条件
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Data
@Schema(description = "考生类型查询条件")
public class CandidateTypeQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @Query(type = QueryType.EQ)
    private Long candidateId;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @QueryIgnore
    private String candidateName;

    /**
     * 考生身份证
     */
    @Schema(description = "考生身份证")
    @QueryIgnore
    private String idNumber;

    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    @QueryIgnore
    private String phone;

    /**
     * 工作单位
     */
    @Schema(description = "工作单位")
    @Query(type = QueryType.LIKE,columns = "twa_latest.work_unit")
    private String workUnit;
}