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

package top.continew.admin.invigilate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 监考员资质证明详情信息
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "监考员资质证明详情信息")
public class UserQualificationDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 八大类ID
     */
    @Schema(description = "八大类ID")
    @ExcelProperty(value = "八大类ID")
    private Long categoryId;

    /**
     * 资质证明URL
     */
    @Schema(description = "资质证明URL")
    @ExcelProperty(value = "资质证明URL")
    private String qualificationUrl;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}