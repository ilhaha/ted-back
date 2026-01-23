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

package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 项目详情信息
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "项目详情信息")
public class ProjectDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @ExcelProperty(value = "项目名称")
    private String projectName;

    @Schema(description = "所属八大类")
    @ExcelProperty(value = "所属八大类")
    private String categoryName;;

    @Schema(description = "所属八大类")
    @ExcelProperty(value = "所属八大类")
    private String categoryId;

    /**
     * 项目代号
     */
    @Schema(description = "项目代号")
    @ExcelProperty(value = "项目代号")
    private String projectCode;

    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    @ExcelProperty(value = "所属部门")
    private String deptName;

    /**
     * 考试时长(分钟)
     */
    @Schema(description = "考试时长(分钟)")
    @ExcelProperty(value = "考试时长(分钟)")
    private Integer examDuration;

    /**
     * 展示图
     */
    @Schema(description = "展示图")
    @ExcelProperty(value = "展示图")
    private String imageUrl;
    /**
     * 项目状态
     */
    @Schema(description = "项目状态")
    @ExcelProperty(value = "项目状态")
    private Long projectStatus;

    /**
     * 项目类型（0-作业人员 1-检验人员）
     */
    @Schema(description = "项目类型（0-作业人员 1-检验人员）")
    @ExcelProperty(value = "项目类型（0-作业人员 1-检验人员）")
    private Integer projectType;


    /**
     * 项目考试等级（ 1一级 2 二级）
     */
    @Schema(description = "项目考试等级（ 1一级 2 二级）")
    @ExcelProperty(value = "项目考试等级（ 1一级 2 二级）")
    private Integer projectLevel;

    /**
     * 是否有实操考试（0无，1有）
     */
    @Schema(description = "是否有实操考试（0无，1有）")
    @ExcelProperty(value = "是否有实操考试（0无，1有）")
    private Integer isOperation;

    /**
     * 描述
     */
    //    @Schema(description = "描述")
    //    @ExcelProperty(value = "描述")
    //    private String redeme;

    /**
     * 删除标记
     */
    //    @Schema(description = "删除标记")
    //    @ExcelProperty(value = "删除标记")
    //    private Boolean isDeleted;

    /**
     * 项目收费标准
     */
    @Schema(description = "项目收费标准")
    @ExcelProperty(value = "项目收费标准")
    private Long examFee;
}