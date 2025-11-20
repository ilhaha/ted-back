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

package top.continew.admin.exam.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 考试计划-项目缴费信息 DTO
 * 封装查询后的考试计划名称、项目名称、缴费金额
 */
@Data
public class ExamPlanProjectPaymentDTO {
    /** 考试计划名称 */
    private String examPlanName;
    /** 项目名称 */
    private String projectName;
    /** 缴费金额（元） */
    private BigDecimal paymentAmount;
    /** 项目编号 */
    private String projectCode;

    public ExamPlanProjectPaymentDTO(String examPlanName,
                                     String projectName,
                                     BigDecimal paymentAmount,
                                     String projectCode) {
        this.examPlanName = examPlanName;
        this.projectName = projectName;
        this.paymentAmount = paymentAmount;
        this.projectCode = projectCode;
    }
}