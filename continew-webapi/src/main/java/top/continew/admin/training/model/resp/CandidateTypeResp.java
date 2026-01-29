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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生类型信息
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Data
@Schema(description = "考生类型信息")
public class CandidateTypeResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    private Long candidateId;

    /**
     * 类型，0作业人员，1检验人员
     */
    @Schema(description = "类型，0作业人员，1检验人员")
    private Boolean type;

    /**
     * 是否黑名单 0-否 1-是
     */
    @Schema(description = "是否黑名单 0-否 1-是")
    private Boolean isBlacklist;

    /**
     * 加入黑名单原因
     */
    @Schema(description = "加入黑名单原因")
    private String blacklistReason;

    /**
     * 黑名单时长类型 0-无 1-1天 2-1个月 3-3个月 4-6个月 5-1年 6-无期限
     */
    @Schema(description = "黑名单时长类型 0-无 1-1天 2-1个月 3-3个月 4-6个月 5-1年 6-无期限")
    private Integer blacklistDurationType;

    /**
     * 加入黑名单时间
     */
    @Schema(description = "加入黑名单时间")
    private LocalDateTime blacklistTime;

    /**
     * 黑名单结束时间
     */
    @Schema(description = "黑名单结束时间")
    private LocalDateTime blacklistEndTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private Long updateUser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Integer isDeleted;

    /**
     * 考试总次数
     */
    @Schema(description = "考试总次数")
    private Integer examTotalCount;

    /**
     * 及格次数
     */
    @Schema(description = "及格次数")
    private Integer passCount;

    /**
     * 不及格次数
     */
    @Schema(description = "不及格次数")
    private Integer failCount;

    /**
     * 未录入成绩次数
     */
    @Schema(description = "未录入成绩次数")
    private Integer unrecordedCount;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String nickname;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String username;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 一寸照
     */
    @Schema(description = "一寸照")
    private String avatar;
}