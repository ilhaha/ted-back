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
 * 项目实体
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@TableName("ted_project")
public class ProjectDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    private String projectName;

    private Long categoryId;

    /**
     * 项目代号
     */
    private String projectCode;

    /**
     * 考试时长(分钟)
     */
    private Integer examDuration;

    /**
     * 描述
     */
    private String redeme;

    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 运行状态
     */
    private Long projectStatus;
    /**
     * 删除标记
     */
    private Boolean isDeleted;

    /**
     * 展示图
     */
    private String imageUrl;

    /**
     * 项目类型（0-作业人员 1-检验人员）
     */
    private Integer projectType;


    /**
     * 项目考试等级（ 0无 1一级 2 二级）
     */
    private Integer projectLevel;

    /**
     * 是否有实操考试（0无，1有）
     */
    private Integer isOperation;

    /**
     * 是否有理论考试（0无，1有）
     */
    private Integer isTheory;

    /**
     * 项目收费标准
     */
    private Long examFee;
}