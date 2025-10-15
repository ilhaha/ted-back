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

package top.continew.admin.examconnect.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.util.List;

/**
 * 创建或修改题库，存储各类题目及其分类信息参数
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Data
@Schema(description = "创建或修改题库，存储各类题目及其分类信息参数")
public class QuestionBankReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属知识类型
     * 长度固定为3
     */
    @NotEmpty(message = "请选择八大类知识类型")
    @Size(min = 3, max = 3, message = "请选择八大类知识格式不正确")
    @Schema(description = "所属八大类知识类型")
    private List<Long> categoryId;

    /**
     * 每项选项
     * 最少有1项选项
     */
    @NotEmpty(message = "选项不能为空")
    @Size(min = 1, message = "选项不能为空")
    @Schema(description = "每项选择")
    private List<String> options;

    /**
     * 题目
     * 不能为空
     */
    @NotBlank(message = "题目不能为空")
    @Schema(description = "题目")
    private String question;

    /**
     * 题目类型
     * 0~3
     */
    @Min(value = 0, message = "非法题目类型")
    @Max(value = 3, message = "非法题目类型")
    @Schema(description = "题目类型")
    private Integer questionType;

    /**
     * 答案
     * 最小长度1
     */
    @NotEmpty(message = "最少要有一个答案")
    @Size(min = 1, message = "最少要有一个答案")
    @Schema(description = "答案")
    private List<Integer> correctAnswers;

    @Schema(description = "图片最小缩略图")
    private String imageMinUrl;

    @Schema(description = "图片最大缩略图")
    private String imageUrl;
}