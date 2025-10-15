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

package top.continew.admin.examconnect.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考生答题，记录考生答题情况实体
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Data
@TableName("ted_exam_answer")
public class ExamAnswerDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 题库ID
     */
    private Long questionId;

    /**
     * 排序（答题顺序）
     */
    private Integer sort;

    /**
     * 步骤ID
     */
    private Long stepId;

    /**
     * 是否正确（0-错误，1-正确）
     */
    private Boolean isCorrect;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;
}