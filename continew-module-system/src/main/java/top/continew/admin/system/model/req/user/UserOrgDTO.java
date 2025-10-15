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

package top.continew.admin.system.model.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserOrgDTO {
    /**
     * 考生id 用于传入数据
     */
    @Schema(description = "考生id")
    private Long id;
    /**
     * 用户名(默认身份证号)
     *
     */
    @Schema(description = "用户名(默认身份证号)")
    @NotNull(message = "用户名不能为空")
    private String username;
    /**
     * 昵称
     */
    @Schema(description = "名称")
    @NotNull(message = "昵称不能为空")
    private String nickname;
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @NotNull(message = "手机号不能为空")
    private String phone;
    /**
     * 密码（加密）机构默认密码
     */
    @Schema(description = "密码（加密）机构默认密码")
    @NotNull(message = "密码不能为空")
    private String password;
    /**
     * 机构代号
     */
    @Schema(description = "机构代号")
    @NotNull(message = "机构代号不能为空")
    private String code;
    /**
     * 机构代号
     */
    @Schema(description = "机构id")
    @NotNull(message = "机构id不能为空")
    private Long orgId;
    /**
     * 角色id
     */
    @Schema(description = "角色id")
    @NotNull(message = "角色id不能为空")
    private Long roleId;
    /**
     * 部门id
     */
    @Schema(description = "部门id")
    @NotNull(message = "部门id不能为空")
    private Long deptId;

}
