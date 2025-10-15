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

/**
 * 学习记录查询条件
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@Schema(description = "学习记录查询条件")
public class WatchRecordQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生名称
     */
    @Schema(description = "学生名称")
    @Query(columns = {"ts.nickname"}, type = QueryType.LIKE)
    private String studentName;

    /**
     * 视频名称
     */
    @Schema(description = "视频ID")
    @Query(columns = {"tv.title"}, type = QueryType.LIKE)
    private String videoName;

    /**
     * 0-未观看 1-已观看 2-已完成
     */
    @Schema(description = "0-未观看 1-已观看 2-已完成")
    @Query(columns = {"twr.status"}, type = QueryType.EQ)
    private Integer status;
}