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

package top.continew.admin.document.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

@Data
public class DocumentUploadResp {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userName;
    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String nickName;

    /**
     * 存储路径(如/img/身份证正面.jpg)
     */
    @Schema(description = "存储路径(如/img/身份证正面.jpg)")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    private Long typeId;
    /**
     * 关联资料类型名称
     */
    @Schema(description = "关联资料类型名称")
    private String typeName;
    /**
     * 审核状态:0:待审核;1:已生效;2:未通过;
     */
    @Schema(description = "审核状态:0:待审核;1:已生效;2:未通过;")
    private Integer status;
}
