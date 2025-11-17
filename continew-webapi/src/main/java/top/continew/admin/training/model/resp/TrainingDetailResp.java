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
import java.math.BigDecimal;

/**
 * 培训主表详情信息
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "培训主表详情信息")
public class TrainingDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训名称
     */
    @Schema(description = "培训名称")
    @ExcelProperty(value = "培训名称")
    private String title;

    /**
     * 封面路径
     */
    @Schema(description = "封面路径")
    @ExcelProperty(value = "封面路径")
    private String coverPath;

    /**
     * 视频总时长（秒）
     */
    @Schema(description = "视频总时长（秒）")
    @ExcelProperty(value = "视频总时长（秒）")
    private Integer totalDuration;

    /**
     * 专家ID
     */
    @Schema(description = "专家ID")
    @ExcelProperty(value = "专家ID")
    private Long expertId;

    @Schema(description = "专家名字")
    @ExcelProperty(value = "专家名字")
    private String expertName;

//    /**
//     * 费用
//     */
//    @Schema(description = "费用")
//    @ExcelProperty(value = "费用")
//    private BigDecimal fee;

    /**
     * 专家费用
     */
    @Schema(description = "专家费用")
    @ExcelProperty(value = "专家费用")
    private BigDecimal expertFee;

    /**
     * 培训描述
     */
    @Schema(description = "培训描述")
    @ExcelProperty(value = "培训描述")
    private String description;

    /**
     * 0-未审核 1-上架 2-下架
     */
    @Schema(description = "0-未审核 1-上架 2-下架")
    @ExcelProperty(value = "0-未审核 1-上架 2-下架")
    private Long status;

    /**
     * 0-未删除 1-已删除
     */
    @Schema(description = "0-未删除 1-已删除")
    @ExcelProperty(value = "0-未删除 1-已删除")
    private Integer isDeleted;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    @ExcelProperty(value = "项目id")
    private Long projectId;
}