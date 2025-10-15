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
 * 视频查询条件
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@Schema(description = "视频查询条件")
public class VideoQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    @Schema(description = "章节ID")
    @Query(type = QueryType.EQ)
    private Long chapterId;

    /**
     * 视频标题
     */
    @Schema(description = "视频标题")
    @Query(type = QueryType.EQ)
    private String title;

    /**
     * 视频时长（秒）
     */
    @Schema(description = "视频时长（秒）")
    @Query(type = QueryType.EQ)
    private Integer duration;

    /**
     * 视频路径
     */
    @Schema(description = "视频路径")
    @Query(type = QueryType.EQ)
    private String videoPath;
}