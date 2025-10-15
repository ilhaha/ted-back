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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;

import top.continew.admin.certificate.model.dto.CertificateInfoDTO;
import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;
import java.util.List;

/**
 * 考生报名表详情信息
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生报名表详情信息")
public class EnrollDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划图片
     */
    @Schema(description = "考试计划图片")
    private String imageUrl;

    /**
     * 考试名称
     */
    @Schema(description = "考试名称")
    private String examPlanName;
    /**
     * 报名截止时间
     */
    @Schema(description = "报名截止时间")
    private LocalDateTime enrollEndTime;
    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    private LocalDateTime examStartTime;
    /**
     * 考试时长
     */
    @Schema(description = "考试时长")
    private Long examDuration;
    /**
     * 考试地点
     */
    @Schema(description = "考试地点")
    private String examPlace;
    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;
    /**
     * 考试费用
     */
    @Schema(description = "考试费用")
    private Long examFee;
    /**
     * 考试描述
     */
    @Schema(description = "考试描述")
    private String redeme;
    /**
     * 证书列表
     */
    @Schema(description = "证书列表")
    private List<CertificateInfoDTO> certificates;
    /**
     * 文档列表
     */
    @Schema(description = "资料列表")
    private List<String> documentNames;
    /**
     * 考试状态
     */
    @Schema(description = "考试状态")
    private String enrollStatus;
}