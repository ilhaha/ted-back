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

import java.util.List;

@Data
public class BatchAuditSpecialCertificationApplicantDTO {

    /**
     * 批量审核的记录ID数组
     */
    private List<String> ids;

    /**
     * 审核状态：1=通过，2=不通过
     */

    private Integer status;

    /**
     * 审核理由
     */
    private String reason;       // 不通过时的原因（可选）
}