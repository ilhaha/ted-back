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
 * 创建或修改专家信息参数
 *
 * @author Anton
 * @since 2025/04/07 10:45
 */
@Data
@Schema(description = "创建或修改专家信息参数")
public class ExpertReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专家名字
     */
    @Schema(description = "专家名字")
    @NotBlank(message = "专家名字不能为空")
    @Length(max = 100, message = "专家名字长度不能超过 {max} 个字符")
    private String name;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    @NotBlank(message = "身份证号码不能为空")
    private String idCard;

    /**
     * 专家学历
     */
    @Schema(description = "专家学历")
    @NotBlank(message = "专家学历不能为空")
    @Length(max = 100, message = "专家学历长度不能超过 {max} 个字符")
    private String education;

    /**
     * 专家职称
     */
    @Schema(description = "专家职称")
    @NotBlank(message = "专家职称不能为空")
    @Length(max = 100, message = "专家职称长度不能超过 {max} 个字符")
    private String title;

}