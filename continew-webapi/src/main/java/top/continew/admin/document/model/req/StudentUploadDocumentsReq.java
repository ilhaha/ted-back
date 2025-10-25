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
import lombok.Data;

import java.io.Serializable;

/**
 * @Author ilhaha
 * @Create 2025/3/13 11:51
 * @Version 1.0
 */
@Data
public class StudentUploadDocumentsReq implements Serializable {

    @Schema(description = "资料路径")
    @NotBlank(message = "资料路径不能为空")
    private String docPath;

    @Schema(description = "资料类型")
    @NotBlank(message = "资料类型不能为空")
    private Long typeId;

    @Schema(description = "考生id")
    private Long candidateId;

    @Schema(description = "资料id")
    private Long Id;

    @Schema(description = "审核状态:0:待审核;1:已生效;2:待补正;3:补正审核;")
    private Integer status;
}
