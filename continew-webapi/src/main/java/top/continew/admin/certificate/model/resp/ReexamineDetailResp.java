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

/**
 * 复审详情信息
 *
 * @author Anton
 * @since 2025/04/29 08:48
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "复审详情信息")
public class ReexamineDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请复审人员id
     */
    @Schema(description = "申请复审人员id")
    @ExcelProperty(value = "申请复审人员id")
    private Long applicantId;

    /**
     * 申请复审人员姓名
     */
    @Schema(description = "申请复审人员姓名")
    private String applicantName;

    /**
     * 申请复审证件id
     */
    @Schema(description = "申请复审证件id")
    @ExcelProperty(value = "申请复审证件id")
    private Long certificateId;
    /**
     * 申请复审证件名称
     */
    @Schema(description = "申请复审证件名称")
    private String certificateName;

    /**
     * 作业人员资格证url
     */
    @Schema(description = "作业人员资格证url")
    @ExcelProperty(value = "作业人员资格证url")
    private String certificateUrl;

    /**
     * 复审资格申请表url
     */
    @Schema(description = "复审资格申请表url")
    @ExcelProperty(value = "复审资格申请表url")
    private String applicantFormUrl;

    /**
     * 审核状态（0：未审核，1：已通过，2：未通过）
     */
    @Schema(description = "审核状态（0：未审核，1：已通过，2：未通过）")
    @ExcelProperty(value = "审核状态（0：未审核，1：已通过，2：未通过）")
    private Integer reexaminStatus;

    /**
     * 逻辑删除
     */
    @Schema(description = "逻辑删除")
    @ExcelProperty(value = "逻辑删除")
    private Integer isDeleted;
}