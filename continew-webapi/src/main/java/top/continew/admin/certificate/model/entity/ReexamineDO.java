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

package top.continew.admin.certificate.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 复审实体
 *
 * @author Anton
 * @since 2025/04/29 08:48
 */
@Data
@TableName("ted_reexamine")
public class ReexamineDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请复审人员id
     */
    private Long applicantId;

    /**
     * 申请复审证件id
     */
    private Long certificateId;

    /**
     * 作业人员资格证url
     */
    private String certificateUrl;

    /**
     * 复审资格申请表url
     */
    private String applicantFormUrl;

    /**
     * 审核状态（0：未审核，1：已通过，2：未通过）
     */
    private Integer reexaminStatus;

    /**
     * 逻辑删除
     */
    private Integer isDeleted;
}