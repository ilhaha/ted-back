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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 资料核心存储信息
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Data
@Schema(description = "资料核心存储信息")
public class DocumentResp extends BaseResp {

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

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    private Boolean isDeleted;


    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createUser;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    private Long candidateId;

    /**
     * 审核备注
     */
    @Schema(description = "审核备注")
    private String auditRemark;


    /**
     * 资料ID
     */
    @Schema(description = "资料ID")
    private Long Id;

}