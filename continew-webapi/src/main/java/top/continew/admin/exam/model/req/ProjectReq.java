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

package top.continew.admin.exam.model.req;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改项目参数
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@Schema(description = "创建或修改项目参数")
public class ProjectReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 八大类ID
     */
    private Long categoryId;;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目代号
     */
    private String projectCode;

    /**
     * 考试时长(分钟)
     */
    private Integer examDuration;
    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 展示图
     */
    private String imageUrl;
    /**
     * 项目状态
     */
    private Long projectStatus;

    /**
     * 项目类型（0-作业人员 1-检验人员）
     */
    private Integer projectType;

    /**
     * 是否有实操考试（0无，1有）
     */
    private Integer isOperation;

    /**
     * 项目收费标准
     */
    private Long examFee;

}