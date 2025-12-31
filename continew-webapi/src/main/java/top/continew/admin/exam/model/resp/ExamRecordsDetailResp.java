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
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试记录详情信息
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试记录详情信息")
public class ExamRecordsDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @ExcelProperty(value = "计划ID")
    private Long planId;
    /**
     * 计划名称（后加）
     */
    @Schema(description = "计划名称")
    private String planName;

    /**
     * 考生姓名（后加）
     */
    @Schema(description = "考生姓名")
    private String candidateName;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long candidateId;

    /**
     * 报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;
     */
    @Schema(description = "报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;")
    @ExcelProperty(value = "报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;")
    private Integer registrationProgress;

    /**
     * 审核进度；0:待审核;1:已审核;2:审核未通过;
     */
    @Schema(description = "审核进度；0:待审核;1:已审核;2:审核未通过;")
    @ExcelProperty(value = "审核进度；0:待审核;1:已审核;2:审核未通过;")
    private Integer reviewStatus;

    /**
     * 考试得分，(项目id_得分#项目id_得分#)
     */
    @Schema(description = "考试得分，(项目id_得分#项目id_得分#)")
    @ExcelProperty(value = "考试得分，(项目id_得分#项目id_得分#)")
    private Integer examScores;

    /**
     * 实操成绩
     */
    @Schema(description = "实操成绩")
    @ExcelProperty(value = "实操成绩")
    private Integer operScores;

    /**
     * 道路成绩
     */
    @Schema(description = "道路成绩")
    @ExcelProperty(value = "道路成绩")
    private Integer roadScores;

    /**
     * 实操成绩录入状态（0未录入，1已录入）
     */
    @Schema(description = "实操成绩录入状态（0未录入，1已录入）")
    @ExcelProperty(value = "实操成绩录入状态（0未录入，1已录入）")
    private Integer operInputStatus;

    /**
     * 道路成绩录入状态（0未录入，1已录入）
     */
    @Schema(description = "道路成绩录入状态（0未录入，1已录入）")
    @ExcelProperty(value = "道路成绩录入状态（0未录入，1已录入）")
    private Integer roadInputStatus;


    /**
     * 证书是否已生成（0：未生成，1：已生成）
     */
    @Schema(description = " 证书是否已生成（0：未生成，1：已生成）")
    @ExcelProperty(value = " 证书是否已生成（0：未生成，1：已生成）")
    private Integer isCertificateGenerated;

    /**
     * 考试结果（0不及格，1及格,2未录入）
     */
    @Schema(description = " 考试结果（0不及格，1及格,2未录入）")
    @ExcelProperty(value = " 考试结果（0不及格，1及格,2未录入）")
    private Integer examResultStatus;

    /**
     * 是否删除，0:未删除，1:已删除
     */
    @Schema(description = "是否删除，0:未删除，1:已删除")
    @ExcelProperty(value = "是否删除，0:未删除，1:已删除")
    private Integer isDeleted;
}