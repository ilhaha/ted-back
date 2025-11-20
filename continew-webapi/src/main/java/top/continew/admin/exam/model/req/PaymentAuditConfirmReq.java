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

package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/11/11 18:55
 */
@Data
public class PaymentAuditConfirmReq {

    /** 考试计划ID */
    @NotNull(message = "二维码已被篡改或参数缺失，请重新获取")
    private Long id;

    /**
     * 缴费凭证URL
     */
    @NotBlank(message = "未上传缴费凭证")
    private String paymentProofUrl;

    /**
     * 缴费凭证审核状态
     */
    private Integer auditStatus;

}
