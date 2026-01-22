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

package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 八大类，存储题目分类信息信息
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
@Data
@Schema(description = "八大类，存储题目分类信息信息")
public class CategoryResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 种类名称
     */
    @Schema(description = "种类名称")
    private String name;

    /**
     * 类别代号
     */
    @Schema(description = "类别代号")
    private String code;

    /**
     * 种类类型
     */
    @Schema(description = "种类类型")
    private Integer categoryType;


    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;

    private Long topicNumber;

    private String videoUrl;

    /**
     * 是否开启电子监考违规行为提醒功能（1开启，0未开启）
     */
    @Schema(description = "是否开启电子监考违规行为提醒功能（1开启，0未开启）")
    private Boolean enableProctorWarning;
}