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

/**
 * 学生培训实体
 *
 * @author Anton
 * @since 2025/03/26 11:52
 */
@Data
@TableName("ted_student_training")
public class StudentTrainingDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 培训ID
     */
    private Long trainingId;

    /**
     * 已学时长（秒）
     */
    private Integer totalDuration;

    /**
     * 0-学习中 1-已完成 2-未开始
     */
    private Integer status;

    /**
     * 0-未删除 1-已删除
     */
    private Integer isDeleted;
}