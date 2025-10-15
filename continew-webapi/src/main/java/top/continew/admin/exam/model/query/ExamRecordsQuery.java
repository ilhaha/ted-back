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
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考试记录查询条件
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Data
@Schema(description = "考试记录查询条件")
public class ExamRecordsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @Query(type = QueryType.EQ)
    private Long planId;

    /**
     * 报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;
     */
    @Schema(description = "报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;")
    @Query(type = QueryType.EQ)
    private Integer registrationProgress;

    /**
     * 审核进度；0:待审核;1:已审核;2:审核未通过;
     */
    @Schema(description = "审核进度；0:待审核;1:已审核;2:审核未通过;")
    @Query(type = QueryType.EQ)
    private Integer reviewStatus;

}