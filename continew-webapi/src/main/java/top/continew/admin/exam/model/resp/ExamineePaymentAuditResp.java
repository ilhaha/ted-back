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

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考生缴费审核信息
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@Schema(description = "考生缴费审核信息")
public class ExamineePaymentAuditResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联考试计划ID
     */
    @Schema(description = "关联考试计划ID")
    private Long examPlanId;

    /**
     * 关联考试计划name
     */
    @Schema(description = "关联考试计划name")
    private String planName;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long examineeId;

    /**
     * 考生name
     */
    @Schema(description = "考生name")
    private String examineeName;

    /**
     * 作业人员班级
     */
    @Schema(description = "作业人员班级")
    private String className;

    /**
     * 关联报名记录ID
     */
    @Schema(description = "关联报名记录ID")
    private Long enrollId;

    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    private String noticeNo;

    /**
     * 缴费金额（元）
     */
    @Schema(description = "缴费金额（元）")
    private BigDecimal paymentAmount;

    /**
     * 缴费时间
     */
    @Schema(description = "缴费时间")
    private LocalDateTime paymentTime;

    /**
     * 缴费凭证URL
     */
    @Schema(description = "缴费凭证URL")
    private String paymentProofUrl;

    /**
     * 缴费通知单URL
     */
    @Schema(description = "缴费通知单URL")
    private String auditNoticeUrl;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回
     */
    @Schema(description = "审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回 ")
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    @Schema(description = "驳回原因")
    private String rejectReason;

    /**
     * 审核人ID
     */
    @Schema(description = "审核人ID")
    private Long auditorId;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0否，1是）
     */
    @Schema(description = "是否删除（0否，1是）")
    private Boolean isDeleted;
}