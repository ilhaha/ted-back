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

package top.continew.admin.worker.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 作业人员准考证详情信息
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "作业人员准考证详情信息")
public class WorkerExamTicketDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    @ExcelProperty(value = "报名id")
    private Long enrollId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @ExcelProperty(value = "作业人员姓名")
    private String candidateName;

    /**
     * 准考证地址
     */
    @Schema(description = "准考证地址")
    @ExcelProperty(value = "准考证地址")
    private String ticketUrl;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Integer isDeleted;
}