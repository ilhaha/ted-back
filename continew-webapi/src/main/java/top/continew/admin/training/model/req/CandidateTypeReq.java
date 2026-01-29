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

package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生类型参数
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Data
@Schema(description = "创建或修改考生类型参数")
public class CandidateTypeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 是否黑名单 0-否 1-是
     */
    private Boolean isBlacklist;

    /**
     * 加入黑名单原因
     */
    private String blacklistReason;

    /**
     * 黑名单时长类型 0-无 1-1天 2-1个月 3-3个月 4-6个月 5-1年 6-无期限
     */
    private Integer blacklistDurationType;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 姓名
     */
    private String nickname;

    /**
     * 身份证号
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 一寸照
     */
    private String avatar;
}