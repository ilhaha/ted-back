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

package top.continew.admin.exam.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 特种设备人员资格申请实体
 *
 * @author Anton
 * @since 2025/04/07 15:43
 */
@Data
@TableName("ted_special_certification_applicant")
public class SpecialCertificationApplicantDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    private Long candidatesId;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 申请表图片url
     */
    private String imageUrl;

    /**
     * 是否删除（0: 否, 1: 是）
     */
    private Boolean isDeleted;
    /**
     * 审核状态（0：未审核，1：审核通过，2：审核不通过）
     */
    private Integer status;
    /**
     * 计划名称
     */
    @TableField(exist = false)
    private String planName;
}