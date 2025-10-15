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

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.time.*;

/**
 * 轮播图管理信息
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Data
@Schema(description = "轮播图管理信息")
public class CarouselResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    private String imageUrl;

    /**
     * 缩略图
     */
    @Schema(description = "缩略图")
    private String imageMinUrl;

    /**
     * 轮播图对应的公告id
     */
    @Schema(description = "轮播图对应的公告id")
    private Long announcementId;

    /**
     * 图片描述
     */
    @Schema(description = "图片描述")
    private String description;

    /**
     * 排序，数值越大越靠前
     */
    @Schema(description = "排序，数值越大越靠前")
    private Integer sortOrder;

    /**
     * 轮播图状态
     */
    @Schema(description = " 轮播图状态")
    private Integer status;

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

    @ExcelProperty(value = "更新人")
    private String updateUserString;

    @ExcelProperty(value = "更新人")
    private String createUserString;
}