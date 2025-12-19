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
 * 八大类，存储题目分类信息实体
 *
 * @author Anton
 * @since 2025/04/07 10:43
 */
@Data
@TableName("ted_category")
public class CategoryDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 种类名称
     */
    private String name;

    /**
     * 类别代号
     */
    private String code;

    private Long topicNumber;

    private String videoUrl;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;


    /**
     * 是否开启电子监考违规行为提醒功能（1开启，0未开启）
     */
    private Boolean enableProctorWarning;
}