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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 特种设备人员资格申请信息
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Data
@Schema(description = "特种设备人员资格申请信息")
public class SpecialCertificationApplicantResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    private Long candidatesId;

    /**
     * 考生名称
     */
    @Schema(description = "考生名称")
    private String candidatesName;

    /**
     * 计划id
     */
    @Schema(description = "计划id")
    private Long planId;
    /**
     * 计划名称
     */
    @Schema(description = "计划名称")
    private String planName;
    /**
     * 申请表图片url
     */
    @Schema(description = "申请表图片url")
    private String imageUrl;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0: 否, 1: 是）
     */
    @Schema(description = "是否删除（0: 否, 1: 是）")
    private Boolean isDeleted;

    /**
     * 审核状态（0：未审核，1：审核通过，2：审核不通过）
     */
    @Schema(description = "审核状态（0：未审核，1：审核通过，2：审核不通过）")
    private Integer status;
}