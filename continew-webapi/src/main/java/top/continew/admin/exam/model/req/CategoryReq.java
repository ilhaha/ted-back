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

package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改八大类，存储题目分类信息参数
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
@Data
@Schema(description = "创建或修改八大类，存储题目分类信息参数")
public class CategoryReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 种类名称
     */
    @Schema(description = "种类名称")
    @NotBlank(message = "种类名称不能为空")
    @Length(max = 255, message = "种类名称长度不能超过 {max} 个字符")
    private String name;

    @Schema(description = "代码")
    @NotBlank(message = "八大类代码不能为空")
    @Length(max = 255, message = "种类名称长度不能超过 {max} 个字符")
    private String code;

    private Long topicNumber;
    private String videoUrl;
}