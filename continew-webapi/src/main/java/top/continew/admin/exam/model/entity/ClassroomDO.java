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
 * 考场实体
 *
 * @author Anton
 * @since 2025/05/14 16:34
 */
@Data
@TableName("ted_classroom")
public class ClassroomDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考场名称
     */
    private String classroomName;

    /**
     * 逻辑删除
     */
    private Integer isDeleted;

    /**
     * 考场类型，0作业人员考场，1检验人员考场
     */
    private Integer classroomType;

    /**
     * 考场考试类型，0理论考试，1实操考试
     */
    private Integer examType;

    /**
     * 最大容纳人数
     */
    private Long maxCandidates;
}