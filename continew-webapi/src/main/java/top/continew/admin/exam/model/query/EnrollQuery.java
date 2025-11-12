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
 * 考生报名表查询条件
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Data
@Schema(description = "考生报名表查询条件")
public class EnrollQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生姓名
     */
    @QueryIgnore
    private String nickName;
    /**
     * 考试计划名称
     */
    @QueryIgnore
    private String planName;

    @Query(type = QueryType.EQ,columns = "te.exam_plan_id")
    private Long planId;

    @Query(type = QueryType.EQ,columns = "toc.id")
    private Long classId;

}