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

package top.continew.admin.document.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DocumentCandidatesResp {
    @Schema(description = "资料种类名称")
    private String TypeName;
    @Schema(description = "审核状态")
    private Long Status;
    @Schema(description = "审核备注")
    private String AuditRemark;
    @Schema(description = "创建人ID")
    private Long CreateUser;
    @Schema(description = "图片url")
    private String DocumentUrl;
    @Schema(description = "审核人")
    private String Reviewer;
    @Schema(description = "审核时间")
    private String ReviewTime;
}
