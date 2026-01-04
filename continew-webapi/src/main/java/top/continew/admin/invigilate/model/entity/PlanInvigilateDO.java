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

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.*;

/**
 * 考试计划监考人员关联实体
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
@Data
@TableName("ted_plan_invigilate")
public class PlanInvigilateDO extends BaseDO {

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
     * 实操考试劳务费单价（元）
     */
    private BigDecimal practicalFee;

    /**
     * 理论考试劳务费单价（元）
     */
    private BigDecimal theoryFee;

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

    /**
     * 监监考状态（0：待监考，1：待录入，2：待审核，3：已完成，4待监考员确认，5监考员拒绝监考）
     */
    private Integer invigilateStatus;

    /**
     * 开考密码
     */
    private String examPassword;

    /**
     * 考场号
     */
    private Long classroomId;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}