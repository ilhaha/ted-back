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
 * 考场详情信息
 *
 * @author Anton
 * @since 2025/05/14 16:34
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考场详情信息")
public class ClassroomDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考场名称
     */
    @Schema(description = "考场名称")
    @ExcelProperty(value = "考场名称")
    private String classroomName;

    /**
     * 逻辑删除
     */
    @Schema(description = "逻辑删除")
    @ExcelProperty(value = "逻辑删除")
    private Integer isDeleted;

    /**
     * 最大容纳人数
     */
    @Schema(description = "最大容纳人数")
    @ExcelProperty(value = "最大容纳人数")
    private Long maxCandidates;

    /**
     * 考试地点ID
     */
    @Schema(description = "考试地点ID")
    private String examLocationId;

    /**
     * 考试地点
     */
    @Schema(description = "考试地点")
    private String examLocation;

    /**
     * 考场类型，0作业人员考场，1检验人员考场
     */
    @Schema(description = "考场类型，0作业人员考场，1检验人员考场")
    private Integer classroomType;

    /**
     * 考场考试类型，0理论考试，1实操考试
     */
    @Schema(description = "考场考试类型，0理论考试，1实操考试")
    private Integer examType;

}