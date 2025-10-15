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

package top.continew.admin.exam.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考试记录实体
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Data
@TableName("ted_exam_records")
public class ExamRecordsDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;
     */
    private Integer registrationProgress;

    /**
     * 考试得分，(项目id_得分#项目id_得分#)
     */
    private String examScores;

    /**
     * 是否删除，0:未删除，1:已删除
     */
    private Integer isDeleted;

    /**
     * 审核进度；0:待审核;1:已审核;2:审核未通过;
     */
    private Integer reviewStatus;

    /**
     * 计划名称（后加）
     */
    @TableField(exist = false)
    private String planName;

    /**
     * 考生姓名（后加）
     */
    @TableField(exist = false)
    private String candidateName;
    /**
     * 考试试卷
     */
    @Schema(description = "考试试卷")
    private String examPaper;
}