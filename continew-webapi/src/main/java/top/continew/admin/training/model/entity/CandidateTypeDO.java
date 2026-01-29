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

package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 考生类型实体
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Data
@TableName("ted_candidate_type")
public class CandidateTypeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 类型，0作业人员，1检验人员
     */
    private Boolean type;

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
     * 加入黑名单时间
     */
    private LocalDateTime blacklistTime;

    /**
     * 黑名单结束时间
     */
    private LocalDateTime blacklistEndTime;

    /**
     * 删除标记
     */
    private Integer isDeleted;
}