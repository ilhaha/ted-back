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

package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改章节表参数
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@Schema(description = "创建或修改章节表参数")
public class ChapterReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 培训ID
     */
    @Schema(description = "培训ID")
    @NotNull(message = "培训ID不能为空")
    private Long trainingId;

    /**
     * 章节父ID
     */
    @Schema(description = "章节父ID")
    private Long parentId;

    /**
     * 章节标题
     */
    @Schema(description = "章节标题")
    @NotBlank(message = "章节标题不能为空")
    @Length(max = 100, message = "章节标题长度不能超过 {max} 个字符")
    private String title;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    private Integer sort;
}