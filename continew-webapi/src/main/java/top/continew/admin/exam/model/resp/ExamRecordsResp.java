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
 * 考试记录信息
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Data
@Schema(description = "考试记录信息")
public class ExamRecordsResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    private Long planId;

    /**
     * 计划名称（后加）
     */
    @Schema(description = "计划名称")
    private String planName;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidateId;

    /**
     * 考生姓名（后加）
     */
    @Schema(description = "考生姓名")
    private String candidateName;

    /**
     * 报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;
     */
    @Schema(description = "报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;")
    private Integer registrationProgress;

    /**
     * 审核进度；0:待审核;1:已审核;2:审核未通过;
     */
    @Schema(description = "审核进度；0:待审核;1:已审核;2:审核未通过;")
    private Integer reviewStatus;

    /**
     * 考试得分，(项目id_得分#项目id_得分#)
     */
    @Schema(description = "考试得分，(项目id_得分#项目id_得分#)")
    private String examScores;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除，0:未删除，1:已删除
     */
    @Schema(description = "是否删除，0:未删除，1:已删除")
    private Integer isDeleted;
    /**
     * 考试试卷
     */
    @Schema(description = "考试试卷")
    private String examPaper;
}