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

package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 视频信息
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@Schema(description = "视频信息")
public class VideoResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    @Schema(description = "章节ID")
    private Long chapterId;

    /**
     * 视频标题
     */
    @Schema(description = "视频标题")
    private String title;

    /**
     * 视频时长（秒）
     */
    @Schema(description = "视频时长（秒）")
    private Integer duration;

    /**
     * 视频路径
     */
    @Schema(description = "视频路径")
    private String videoPath;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    private Integer sort;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 0-未删除 1-已删除
     */
    @Schema(description = "0-未删除 1-已删除")
    private Integer isDeleted;
}