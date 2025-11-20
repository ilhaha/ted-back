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

package top.continew.admin.exam.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.starter.security.crypto.annotation.FieldEncrypt;

import java.util.List;

@Data

public class EnrollInfoResp {
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "资料列表")
    private List<String> documentList;

    //    @FieldEncrypt
    @Schema(description = "手机号码")
    private String phoneNumber;

    @FieldEncrypt
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "职位")
    private String position;
}
