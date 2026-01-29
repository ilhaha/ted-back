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

/**
 * 考生报名表实体
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Data
@TableName("ted_enroll")
public class EnrollDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 考生id
     */
    private Long userId;

    /**
     * 考试计划id
     */
    private Long examPlanId;

    /**
     * 项目id
     */
    @TableField(exist = false)
    private Long examProjectId;

    /**
     * 报名状态（0：未报名，1：已报名：2：已完成，3：已过期）
     */
    private Integer enrollStatus;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
    /**
     * 考场id
     */
    private Long classroomId;
    /**
     * 座位id
     */
    private Long seatId;
    /**
     * 准考证号
     */
    private String examNumber;

    /**
     * 考试状态（0未签到、1已签到、2已交卷、3缺考、4正在考试、5有违规操作、6补考、7补考正在考试）
     */
    private Integer examStatus;

    /**
     * 是否补考（0否、1是）
     */
    private Integer isMakeup;

    /**
     * 作业人员班级id，检验人员为空
     */
    private Long classId;

    /**
     * 是否复用理论成绩（0=否, 1=是）
     */
    private Integer theoryScoreReused;
}