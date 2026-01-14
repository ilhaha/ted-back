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

package top.continew.admin.training.model.req;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2026/1/9 11:36
 */
@Data
public class OrgClassPaymentUpdateReq {

    /**
     * 班级id
     */
    private Long id;

    /**
     * 缴费状态
     * 0-未缴费 1-已缴费 2-免费 3-审核未通过
     */
    private Integer payStatus;

    /**
     * 缴费凭证URL
     */
    private String payProofUrl;

}
