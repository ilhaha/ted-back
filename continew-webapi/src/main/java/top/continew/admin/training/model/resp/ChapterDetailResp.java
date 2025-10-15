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

package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 章节表详情信息
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "章节表详情信息")
public class ChapterDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训ID
     */
    @Schema(description = "培训ID")
    @ExcelProperty(value = "培训ID")
    private Long trainingId;

    /**
     * 章节标题
     */
    @Schema(description = "章节标题")
    @ExcelProperty(value = "章节标题")
    private String title;

    /**
     * 父章节ID
     */
    @Schema(description = "父章节ID")
    @ExcelProperty(value = "父章节ID")
    private Long parentId;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    @ExcelProperty(value = "排序序号")
    private Integer sort;

    /**
     * 0-未删除 1-已删除
     */
    @Schema(description = "0-未删除 1-已删除")
    @ExcelProperty(value = "0-未删除 1-已删除")
    private Integer isDeleted;
}