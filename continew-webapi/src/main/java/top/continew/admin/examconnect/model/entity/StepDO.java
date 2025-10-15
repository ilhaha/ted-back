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
 * 步骤，存储题目的不同回答步骤实体
 *
 * @author Anton
 * @since 2025/04/07 10:42
 */
@Data
@TableName("ted_step")
public class StepDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 问题内容
     */
    private String question;

    /**
     * 所属题库ID
     */
    private Long questionBankId;

    /**
     * 是否正确答案（0-否，1-是）
     */
    private Boolean isCorrectAnswer;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;
}