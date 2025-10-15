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

package top.continew.admin.certificate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 证件种类详情信息
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "证件种类详情信息")
public class CertificateTypeDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 证件图片路径
     */
    @Schema(description = "证件图片路径")
    private String imageUrl;

    /**
     * 证件名称
     */
    @Schema(description = "证件名称")
    @ExcelProperty(value = "证件名称")
    private String certificateName;

    /**
     * 所属项目ID
     */
    @Schema(description = "所属项目ID")
    private Long belongingProjectId;

    /**
     * 所属项目名称
     */
    @Schema(description = "所属项目名称")
    private String belongingProjectName;

    /**
     * 是否删除，0:未删除，1:已删除
     */
    @Schema(description = "是否删除，0:未删除，1:已删除")
    @ExcelProperty(value = "是否删除，0:未删除，1:已删除")
    private Integer isDeleted;
}