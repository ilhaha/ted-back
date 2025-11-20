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

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/11 15:47
 */
@Data
public class PaymentInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    private String noticeNo;

    /**
     * 缴费凭证URL
     */
    private String paymentProofUrl;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回
     *
     */
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 八大类名称
     */
    private String categoryName;

    /**
     * 姓名
     */
    private String nickname;

}
