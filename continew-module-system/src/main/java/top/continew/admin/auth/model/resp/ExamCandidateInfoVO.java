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

package top.continew.admin.auth.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author ilhaha
 * @Create 2025/5/22 09:13
 * @Version 1.0
 */
@Data
public class ExamCandidateInfoVO implements Serializable {

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID", example = "45")
    private Long planId;

    /**
     * 准考证号
     */
    @Schema(description = "准考证号", example = "202505150956")
    private String examNumber;

    /**
     * 考试时间
     */
    @Schema(description = "考试时间", example = "2025-05-15 15:00:00 —— 2025-05-15 17:00:00")
    private String examTime;

    /**
     * 计划名称
     */
    @Schema(description = "计划名称", example = "一级挖掘机考试")
    private String planName;

    /**
     * 考场ID
     */
    @Schema(description = "考场ID", example = "1")
    private Long classroomId;

    /**
     * 考场名称
     */
    @Schema(description = "考场名称", example = "考场01")
    private String classroomName;

    /**
     * 警示短片
     */
    @Schema(description = "警示短片", example = "警示短片")
    private String warningShortFilm;

    /**
     * 是否开启违规行为提醒
     */
    @Schema(description = "是否开启违规行为提醒", example = "是否开启违规行为提醒")
    private Boolean enableProctorWarning;

    /**
     * 考试时长
     */
    @Schema(description = "考试时长", example = "考试时长")
    private Integer examDuration;

    /**
     * 一寸照
     */
    @Schema(description = "一寸照", example = "一寸照")
    private String facePhoto;
}
