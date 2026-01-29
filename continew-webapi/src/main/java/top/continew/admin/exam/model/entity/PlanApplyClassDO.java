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

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考试计划报考班级实体
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Data
@TableName("ted_plan_apply_class")
public class PlanApplyClassDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 是否已确认成绩（0：未确认 1：已确认）
     */
    private Integer isScoreConfirmed;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
}