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
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试地点信息
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
@Data
@Schema(description = "考试地点信息")
public class ExamLocationResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "考试地点名称")
    @ExcelProperty(value = "考试地点名称")
    private String locationName;

    /**
     * 省份ID
     */
    @Schema(description = "省份ID")
    private Long provinceId;
    /**
     * 省份名称
     */
    @Schema(description = "省份名称")
    private String provinceName;
    /**
     * 城市ID
     */
    @Schema(description = "城市ID")
    private Long cityId;
    /**
     * 城市名称
     */
    @Schema(description = "城市名称")
    private String cityName;

    /**
     * 街道名称
     */
    @Schema(description = "街道名称")
    private String streetName;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String detailedAddress;

    /**
     * 运营状态; 0:运营;1:休息;2:维护;3:关闭;
     */
    @Schema(description = "运营状态; 0:运营;1:休息;2:维护;3:关闭;")
    private Integer operationalStatus;

    //    /**
    //     * 描述
    //     */
    //    @Schema(description = "描述")
    //    private String redeme;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间戳
     */
    @Schema(description = "更新时间戳")
    private LocalDateTime updateTime;

    //    /**
    //     * 删除标记
    //     */
    //    @Schema(description = "删除标记")
    //    private Boolean isDeleted;
}