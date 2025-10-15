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

/**
 * 专家信息详情信息
 *
 * @author Anton
 * @since 2025/04/07 10:45
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "专家信息详情信息")
public class ExpertDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专家名字
     */
    @Schema(description = "专家名字")
    @ExcelProperty(value = "专家名字")
    private String name;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @ExcelProperty(value = "身份证号")
    private String idCard;

    /**
     * 学历
     */
    @Schema(description = "学历")
    @ExcelProperty(value = "学历")
    private String education;

    /**
     * 专家称号
     */
    @Schema(description = "专家称号")
    @ExcelProperty(value = "专家称号")
    private String title;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String orgName;

    /**
     * 头像路径
     */
    @Schema(description = "头像路径")
    @ExcelProperty(value = "头像路径")
    private String avatar;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}