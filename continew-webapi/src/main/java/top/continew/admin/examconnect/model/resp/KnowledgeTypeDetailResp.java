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

package top.continew.admin.examconnect.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 知识类型，存储不同类型的知识占比详情信息
 *
 * @author Anton
 * @since 2025/04/07 10:39
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "知识类型，存储不同类型的知识占比详情信息")
public class KnowledgeTypeDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    @ExcelProperty(value = "项目ID")
    private Long projectId;

    /**
     * 项目ID
     */
    @Schema(description = "项目名称")
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 知识类型名称
     */
    @Schema(description = "知识类型名称")
    @ExcelProperty(value = "知识类型名称")
    private String name;

    /**
     * 占比（百分比）
     */
    @Schema(description = "占比（百分比）")
    @ExcelProperty(value = "占比（百分比）")
    private Integer proportion;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;

    @Schema(description = "创建人")
    @ExcelProperty(value = "创建人")
    private String createUserStr;

    @Schema(description = "更新人")
    @ExcelProperty(value = "更新人")
    private String updateUserStr;
}