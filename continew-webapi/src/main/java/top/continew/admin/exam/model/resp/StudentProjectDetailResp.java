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

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.util.List;

@Data
public class StudentProjectDetailResp {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @ExcelProperty(value = "项目名称")
    private String projectName;

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
     * 描述
     */
    @Schema(description = "描述")
    @ExcelProperty(value = "描述")
    private String redeme;
    /**
     * 资料列表
     */
    @Schema(description = "资料列表")
    @ExcelProperty(value = "资料列表")
    private List<String> documentList;
    /**
     * 地点列表
     */
    @Schema(description = "地点列表")
    @ExcelProperty(value = "地点列表")
    private List<String> locationList;
}
