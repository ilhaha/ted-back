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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建或修改资料类型主参数
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Data
@Schema(description = "创建或修改资料类型主参数")
public class DocumentTypeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称(如身份证/学历证书)
     */
    @Schema(description = "类型名称(如身份证/学历证书)")
    @NotBlank(message = "类型名称(如身份证/学历证书)不能为空")
    @Length(max = 50, message = "类型名称(如身份证/学历证书)长度不能超过 {max} 个字符")
    private String typeName;

    /**
     * 必须上传人员（0全部都需要上传，1京籍上传、2非京籍上传）
     */
    @NotNull(message = "资料上传适用人员")
    private Integer needUploadPerson;

    /**
     * 创建人ID
     */
    
    @Schema(description = "创建人ID")
    //    @NotNull(message = "创建人ID不能为空")
    private Long createUser;
}