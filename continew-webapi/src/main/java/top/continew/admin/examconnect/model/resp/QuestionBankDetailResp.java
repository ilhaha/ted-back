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
import java.util.List;

/**
 * 题库，存储各类题目及其分类信息详情信息
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "题库，存储各类题目及其分类信息详情信息")
public class QuestionBankDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 八大类ID
     */
    @Schema(description = "八大类ID")
    @ExcelProperty(value = "八大类ID")
    private Long categoryId;

    @Schema(description = "每项选项")
    @ExcelProperty(value = "每项选项")
    private List<String> options;

    @Schema(description = "答案")
    @ExcelProperty(value = "答案")
    private List<Integer> correctAnswers;

    /**
     * 八大类子ID
     */
    @Schema(description = "八大类子ID")
    @ExcelProperty(value = "八大类子ID")
    private Long subCategoryId;

    /**
     * 知识类型ID
     */
    @Schema(description = "知识类型ID")
    @ExcelProperty(value = "知识类型ID")
    private Long knowledgeTypeId;

    @Schema(description = "反显的只是类型id")
    @ExcelProperty(value = "反显的只是类型id")
    private Long categoryIds;

    /**
     * 题目内容
     */
    @Schema(description = "题目内容")
    @ExcelProperty(value = "题目内容")
    private String question;

    /**
     * 题目类型(0: 选择; 1: 判断; 2: 多选; 3: 简答)
     */
    @Schema(description = "题目类型(0: 选择; 1: 判断; 2: 多选; 3: 简答)")
    @ExcelProperty(value = "题目类型(0: 选择; 1: 判断; 2: 多选; 3: 简答)")
    private Integer questionType;

    /**
     * 题目附件（图片路径）
     */
    @Schema(description = "题目附件（图片路径）")
    @ExcelProperty(value = "题目附件（图片路径）")
    private String attachment;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}