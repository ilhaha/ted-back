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

package top.continew.admin.invigilate.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.starter.extension.crud.model.entity.BaseIdDO;

import java.io.Serial;
import java.time.*;

/**
 * 考试计划监考人员关联实体
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
@Data
@TableName("ted_plan_invigilate")
public class PlanInvigilateDO extends BaseIdDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划id
     */
    private Long examPlanId;

    /**
     * 监考人员id
     */
    private Long invigilatorId;

    /**
     * 考试开始时间
     */
    @TableField(exist = false)
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @TableField(exist = false)
    private LocalDateTime endTime;

    //    /**
    //     * 监考状态（0：未确认，1：已确认，2：已完成）
    //     */
    //    private Integer invigilateStatus;
    /**
     * 监考状态（0：未监考，1：待录入，2：待审核， 3：已完成）
     */
    private Integer invigilateStatus;

    /**
     * 开考密码
     */
    private String examPassword;

    /**
     * 考场号
     */
    private String classroomId;
}