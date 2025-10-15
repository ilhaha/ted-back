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
import java.util.List;

/**
 * 创建或修改机构信息参数
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
@Data
@Schema(description = "创建或修改机构信息参数")
public class OrgReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 机构代号
     */
    @Schema(description = "机构代号")
    @NotBlank(message = "机构代号不能为空")
    @Length(max = 50, message = "机构代号长度不能超过 {max} 个字符")
    private String code;

    @Schema(description = "机构名称")
    @NotBlank(message = "机构名称不能为空")
    @Length(max = 255, message = "机构名称长度不能超过 {max} 个字符")
    private String name;

    @Schema(description = "机构八大类归属")
    private List<Long> categoryIds;

    @Schema(description = "社会统一代码")
    @NotBlank(message = "社会统一代码不能为空")
    @Length(max = 50, message = "社会统一代码长度不能超过 {max} 个字符")
    private String socialCode;

    @Schema(description = "地点")
    @NotBlank(message = "地点不能为空")
    @Length(max = 255, message = "地点长度不能超过 {max} 个字符")
    private String location;

    @Schema(description = "法人")
    @NotBlank(message = "法人不能为空")
    @Length(max = 100, message = "法人长度不能超过 {max} 个字符")
    private String legalPerson;

    @Schema(description = "公司规模大小")
    @NotBlank(message = "公司规模大小不能为空")
    @Length(max = 50, message = "公司规模大小长度不能超过 {max} 个字符")
    private String scale;

    @Schema(description = "营业执照路径")
    private String businessLicense;
}