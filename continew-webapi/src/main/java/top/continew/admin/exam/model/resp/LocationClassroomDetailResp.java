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

/**
 * 考场地点和考场关联详情信息
 *
 * @author Anton
 * @since 2025/05/14 11:49
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考场地点和考场关联详情信息")
public class LocationClassroomDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地点id
     */
    @Schema(description = "地点id")
    @ExcelProperty(value = "地点id")
    private Integer locationId;

    /**
     * 考场id
     */
    @Schema(description = "考场id")
    @ExcelProperty(value = "考场id")
    private Integer classroomId;
}