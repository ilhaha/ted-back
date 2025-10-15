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

package top.continew.admin.document.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改资料核心存储参数
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Data
@Schema(description = "创建或修改资料核心存储参数")
public class DocumentReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储路径(如/img/身份证正面.jpg)
     */
    @Schema(description = "存储路径(如/img/身份证正面.jpg)")
    @NotBlank(message = "存储路径(如/img/身份证正面.jpg)不能为空")
    @Length(max = 255, message = "存储路径(如/img/身份证正面.jpg)长度不能超过 {max} 个字符")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    @NotNull(message = "关联资料类型ID不能为空")
    private Long typeId;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createUser;
    /**
     * 资料状态
     */
    @Schema(description = "资料状态")
    private Integer status = 0;
    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID")
    private Long updateUser;
}