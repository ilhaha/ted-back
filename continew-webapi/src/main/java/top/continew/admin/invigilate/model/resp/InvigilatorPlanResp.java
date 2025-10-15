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

package top.continew.admin.invigilate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.time.*;

/**
 * 考生报名表信息
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Data
@Schema(description = "考生报名表信息")
public class InvigilatorPlanResp {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 考试计划id
     */
    @Schema(description = "考试计划id")
    private String id;
    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    private String planName;
    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    private LocalDateTime startTime;
    /**
     * 报名截止时间
     */
    @Schema(description = "报名截止时间")
    private LocalDateTime endTime;

    /**
     * 计划图片url
     */
    @Schema(description = "计划图片url")
    private String imageUrl;

}