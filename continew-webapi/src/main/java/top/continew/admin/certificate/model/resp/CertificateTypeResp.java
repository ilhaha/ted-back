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

package top.continew.admin.certificate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 证件种类信息
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Data
@Schema(description = "证件种类信息")
public class CertificateTypeResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 证件名称
     */
    @Schema(description = "证件名称")
    private String certificateName;
    /**
     * 所属项目
     */
    @Schema(description = "所属项目")
    private String projectName;
    /**
     * 证件类型
     */
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除，0:未删除，1:已删除
     */
    @Schema(description = "是否删除，0:未删除，1:已删除")
    private Integer isDeleted;
    /**
     * 展示图
     */
    @Schema(description = "展示图URL")
    private String imageUrl;
}