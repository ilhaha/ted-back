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
import java.math.BigDecimal;

/**
 * 培训主表信息
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@Schema(description = "培训主表信息")
public class TrainingResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训名称
     */
    @Schema(description = "培训名称")
    private String title;

    /**
     * 封面路径
     */
    @Schema(description = "封面路径")
    private String coverPath;

    /**
     * 视频总时长（秒）
     */
    @Schema(description = "视频总时长（秒）")

    private Integer totalDuration;

    /**
     * 专家ID
     */
    @Schema(description = "专家ID")
    private Long expertId;

    @Schema(description = "专家名字")
    private String expertName;

//    /**
//     * 费用
//     */
//    @Schema(description = "费用")
//    private BigDecimal fee;

    /**
     * 培训描述
     */
    @Schema(description = "培训描述")
    private String description;

    /**
     * 0-未审核 1-上架 2-下架
     */
    @Schema(description = "0-未审核 1-上架 2-下架")
    private Long status;

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

    private String learningTime;//已经学习时长
    private Double learningPercentage;//已经学习百分比
    private LocalDateTime payDeadlineTime;

    private Long projectId;
    private String projectName;
}