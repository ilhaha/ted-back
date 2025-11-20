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

package top.continew.admin.document.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构报考-考生扫码上传文件信息
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "机构报考-考生扫码上传文件信息")
public class EnrollPreUploadResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @ExcelProperty(value = "考生姓名")
    private String nickname;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @ExcelProperty(value = "考试计划名称")
    private String examPlanName;

    /**
     * 考试计划id
     */
    @Schema(description = "考试计划id")
    @ExcelProperty(value = "考试计划id")
    private String planId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String orgName;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String batchId;

    /**
     * 报名资格申请表URL
     */
    @Schema(description = "报名资格申请表URL")
    @ExcelProperty(value = "报名资格申请表URL")
    private String qualificationFileUrl;

    /**
     * 审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）
     */
    @Schema(description = "审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）")
    @ExcelProperty(value = "审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）")
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    @ExcelProperty(value = "审核意见或退回原因")
    private String remark;

    /**
     * 是否删除（0否，1是）
     */
    @Schema(description = "是否删除（0否，1是）")
    @ExcelProperty(value = "是否删除（0否，1是）")
    private Boolean isDeleted;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidatesId;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}