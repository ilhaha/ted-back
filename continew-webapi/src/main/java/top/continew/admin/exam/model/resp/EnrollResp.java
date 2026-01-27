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

import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生报名表信息
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Data
@Schema(description = "考生报名表信息")
public class EnrollResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private Long id;
    /**
     * 考试计划id
     */
    @Schema(description = "考试计划id")
    private String examPlanId;
    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    private String examPlanName;
    /**
     * 考试项目id
     */
    @Schema(description = "考试项目id")
    private String examProjectId;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private GenderEnum gender;

    /**
     * 考试项目代码
     */
    @Schema(description = "考试项目代码")
    private String projectCode;
    /**
     * 申请考核项目种类
     */
    @Schema(description = "申请考核项目种类")
    private String categoryName;
    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    private LocalDateTime examStartTime;
    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间")
    private LocalDateTime examEndTime;

    /**
     * 正脸照
     */
    @Schema(description = "正脸照")
    private String facePhoto;
    /**
     * 报名截止时间
     */
    @Schema(description = "报名截止时间")
    private LocalDateTime enrollEndTime;

    /**
     * 计划图片url
     */
    @Schema(description = "计划图片url")
    private String imageUrl;
    /**
     * 考生名称
     */
    @Schema(description = "考生名称")
    private String nickName;
    /**
     * 考生身份证号
     */
    @Schema(description = "考生身份证号")
    private String username;
    /**
     * 考场号
     */
    @Schema(description = "考场名称")
    private String classroomName;
    /**
     * 座位号
     */
    @Schema(description = "座位号")
    private String seatId;
    /**
     * 准考证号
     */
    @Schema(description = "准考证号")
    private String examNumber;

    /**
     * 报考班级名称
     */
    @Schema(description = "报考班级名称")
    private String className;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String orgName;

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
     *
     */
    @Schema(description = "审核状态")
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    @Schema(description = "驳回原因")
    private String rejectReason;

    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）")
    private String noticeNo;

    /**
     * 准考证
     */
    private String ticketUrl;

    /**
     * 是否复用理论成绩（0=否, 1=是）
     */
    private Integer theoryScoreReused;

    /**
     * 报名状态（0：未报名，1：已报名：2：已完成，3：已过期）
     */
    private Integer enrollStatus;
}