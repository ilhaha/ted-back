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

package top.continew.admin.common.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import java.io.Serial;
import java.time.*;

/**
 * 轮播图管理详情信息
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "轮播图管理详情信息")
public class CarouselDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    @ExcelProperty(value = "图片地址")
    private String imageUrl;

    /**
     * 缩略图
     */
    @Schema(description = "缩略图")
    @ExcelProperty(value = "缩略图")
    private String imageMinUrl;

    /**
     * 轮播图对应的公告id
     */
    @Schema(description = "轮播图对应的公告id")
    @ExcelProperty(value = "轮播图对应的公告id")
    private Long announcementId;

    /**
     * 图片描述
     */
    @Schema(description = "图片描述")
    @ExcelProperty(value = "图片描述")
    private String description;

    /**
     * 排序，数值越大越靠前
     */
    @Schema(description = "排序，数值越大越靠前")
    @ExcelProperty(value = "排序，数值越大越靠前")
    private Integer sortOrder;

    /**
     * 轮播图状态
     */
    @Schema(description = " 轮播图状态")
    @ExcelProperty(value = " 轮播图状态")
    private Integer status;

    @Schema(description = " 更新人")
    @ExcelProperty(value = "更新人")
    private String updateUserString;

    @Schema(description = " 创建人")
    @ExcelProperty(value = "创建人")
    private String createUserString;

    @Schema(description = "公告标题")
    @ExcelProperty(value = "公告标题")
    private String announcementTitle;

    //    /**
    //     * 删除标记
    //     */
    //    @Schema(description = "删除标记")
    //    @ExcelProperty(value = "删除标记")
    //    private Boolean isDeleted;
}