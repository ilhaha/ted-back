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

package top.continew.admin.certificate.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 考生证件实体
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Data
@TableName("ted_candidate_certificate")
public class CandidateCertificateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 证件种类ID
     */
    @Schema(description = "证件种类ID")
    @ExcelProperty(value = "证件种类ID")
    private Long certificateTypeId;

    /**
     * 证件状态，0:未持有;1:持有;2:到期;3:待换证
     */
    private Integer certificateStatus;

    /**
     * 证件编号
     */
    private String certificateNumber;

    /**
     * 颁发人ID
     */
    private Long issuerId;

    /**
     * 持有时间
     */
    private LocalDate holdingDate;

    /**
     * 到期时间
     */
    private LocalDate expiryDate;

    /**
     * 是否删除，0:未删除，1:已删除
     */
    private Integer isDeleted;
}