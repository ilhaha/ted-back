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

package top.continew.admin.training.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrgCandidatesResp {
    @Schema(description = "考生id")
    private Long candidateId;
    //    @FieldEncrypt
    @Schema(description = "考生手机号")
    private String phoneNumber;
    @Schema(description = "考生昵称")
    private String nickName;
    @Schema(description = "机构id")
    private Long orgId;
    @Schema(description = "项目id")
    private Long projectId;
    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "身份证正面")
    private String idCardPhotoFront;
    @Schema(description = "身份证反面")
    private String idCardPhotoBack;
}
