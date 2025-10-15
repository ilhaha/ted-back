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
 * 公告管理详情信息
 *
 * @author ilahha
 * @since 2025/03/19 16:02
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "公告管理详情信息")
public class AnnouncementDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题")
    @ExcelProperty(value = "公告标题")
    private String title;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容")
    @ExcelProperty(value = "公告内容")
    private String content;

    /**
     * 公告状态
     */
    @Schema(description = "公告状态")
    @ExcelProperty(value = "公告状态")
    private Integer status;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Boolean isDeleted;
}