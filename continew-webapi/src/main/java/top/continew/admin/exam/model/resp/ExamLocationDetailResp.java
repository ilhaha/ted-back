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
 * 考试地点详情信息
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试地点详情信息")
public class ExamLocationDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "考试地点名称")
    @ExcelProperty(value = "考试地点名称")
    private String locationName;

    /**
     * 省份ID
     */
    @Schema(description = "省份ID")
    @ExcelProperty(value = "省份ID")
    private Long provinceId;

    @Schema(description = "省份名称")
    @ExcelProperty(value = "省份名称")
    private String provinceName;

    @Schema(description = "街道名称")
    @ExcelProperty(value = "街道名称")
    private String streetName;

    @Schema(description = "街道ID")
    @ExcelProperty(value = "街道ID")
    private Long streetId;

    /**
     * 城市ID
     */
    @Schema(description = "城市ID")
    @ExcelProperty(value = "城市ID")
    private Long cityId;

    @Schema(description = "城市名称")
    @ExcelProperty(value = "城市名称")
    private String cityName;

    //    /**
    //     * 街道ID
    //     */
    //    @Schema(description = "街道ID")
    //    @ExcelProperty(value = "街道ID")
    //    private Long streetId;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    @ExcelProperty(value = "详细地址")
    private String detailedAddress;

    /**
     * 运营状态; 0:运营;1:休息;2:维护;3:关闭;
     */
    @Schema(description = "运营状态; 0:运营;1:休息;2:维护;3:关闭;")
    @ExcelProperty(value = "运营状态; 0:运营;1:休息;2:维护;3:关闭;")
    private Integer operationalStatus;

    //    /**
    //     * 描述
    //     */
    //    @Schema(description = "描述")
    //    @ExcelProperty(value = "描述")
    //    private String redeme;

    //    /**
    //     * 删除标记
    //     */
    //    @Schema(description = "删除标记")
    //    @ExcelProperty(value = "删除标记")
    //    private Boolean isDeleted;
    //    /**
    //     * 更新人ID
    //     */
    //    @Schema(description = "更新人ID")
    //    private Long updateUser;

}