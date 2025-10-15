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

package top.continew.admin.exam.model.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * 考试地点详情信息
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试计划对应考试地点信息和考场信息")
public class PlanLocationAndRoomVO extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "考试地点id")
    @ExcelProperty(value = "考试地点id")
    private Long locationId;


    @Schema(description = "考试地点名称")
    @ExcelProperty(value = "考试地点名称")
    private String locationName;

    @Schema(description = "考试地点完整地址")
    @ExcelProperty(value = "考试地点完整地址")
    private String fullAddress;

    /**
     * 考场id
     */
    @Schema(description = "考场id")
    @ExcelProperty(value = "考场id")
    private Long classroomId;

    /**
     * 考场名称
     */
    @Schema(description = "考场名称")
    @ExcelProperty(value = "考场名称")
    private String classroomName;

    /**
     * 考场最大容纳人数
     */
    @Schema(description = "考场最大容纳人数")
    @ExcelProperty(value = "考场最大容纳人数")
    private Integer maxCandidates;

}