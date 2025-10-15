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

package top.continew.admin.examconnect.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 步骤，存储题目的不同回答步骤详情信息
 *
 * @author Anton
 * @since 2025/04/07 10:42
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "步骤，存储题目的不同回答步骤详情信息")
public class StepDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 问题内容
     */
    @Schema(description = "问题内容")
    @ExcelProperty(value = "问题内容")
    private String question;

    /**
     * 所属题库ID
     */
    @Schema(description = "所属题库ID")
    @ExcelProperty(value = "所属题库ID")
    private Long questionBankId;

    /**
     * 是否正确答案（0-否，1-是）
     */
    @Schema(description = "是否正确答案（0-否，1-是）")
    @ExcelProperty(value = "是否正确答案（0-否，1-是）")
    private Boolean isCorrectAnswer;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}