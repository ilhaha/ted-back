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

package top.continew.admin.system.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExamStaffFeeSummaryVO {
    /**
     * 监考人ID
     */
    private Long invigilatorId;
    /**
     * 监考人姓名
     */
    private String nickname;
    /**
     * 总费用
     */
    private BigDecimal totalFee;
    /**
     * 代扣税金
     */
    private BigDecimal taxFee;
    /**
     * 实发金额
     */
    private BigDecimal actualFee;
    /**
     * 监考内容
     */
    private String examContent;
    /** 监考员身份证号 */
    private String invigilatorIdNumber;

    /** 监考员开户银行名称 */
    private String invigilatorBankName;

    /** 监考员银行卡号 */
    private String invigilatorBankAccount;

    /** 监考员所属单位 */
    private String invigilatorUnit;

    /** 监考员职称 */
    private String invigilatorTitle;

    /**
     * 联系方式
     */
    private String phone;
}