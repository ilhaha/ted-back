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

package top.continew.admin.training.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 培训机构班级信息
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@Schema(description = "培训机构班级信息")
public class OrgClassResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @Schema(description = "机构id")
    private Long orgId;

    /**
     * 机构代码
     */
    @Schema(description = "机构代码")
    private String orgCode;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String orgName;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private Long projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    private String className;

    /**
     * 作业人员扫码报考二维码
     */
    @Schema(description = "作业人员扫码报考二维码")
    private String qrcodeApplyUrl;

    /**
     * 班级类型，0作业人员班级，1检验人员班级
     */
    @Schema(description = "班级类型，0作业人员班级，1检验人员班级")
    private Integer classType;

    /**
     * 状态，0招生找，1停止招生
     */
    @Schema(description = "状态，0招生找，1停止招生")
    private Integer status;

    /**
     * 更新人id
     */
    @Schema(description = "更新人id")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @Schema(description = "是否删除 0-未删除 1-已删除")
    private Integer isDeleted;

    /**
     * 作业人员总人数
     */
    @Schema(description = "作业人员总人数")
    private Long workerCount;

    /**
     * 作业人员资料待审核人数
     */
    @Schema(description = "作业人员资料待审核人数")
    private Long pendingReviewCount;

    /**
     * 作业人员资料审核通过人数
     */
    @Schema(description = "作业人员资料审核通过人数")
    private Long approvedCount;

    /**
     * 作业人员资料审核未通过人数
     */
    @Schema(description = "作业人员资料审核未通过人数")
    private Long rejectedCount;

    /**
     * 作业人员待上传资料人数
     */
    @Schema(description = "作业人员待上传资料人数")
    private Long waitUploadCount;

    /**
     * 作业人员资料待补全人数
     */
    @Schema(description = "作业人员资料待补全人数")
    private Long waitCompleteCount;

    /**
     * 作业人员已上传资料人数
     */
    @Schema(description = "作业人员已上传资料人数")
    private Long uploadedCount;

    /**
     * 资料提交状态
     * 0-未提交 1-已提交
     */
    @Schema(description = "资料提交状态 0-未提交 1-已提交")
    private Integer docSubmitStatus;

    /**
     * 资料提交时间
     */
    @Schema(description = "资料提交时间")
    private LocalDateTime docSubmitTime;

    /**
     * 缴费状态（ 0未缴费 1待审核 2已缴费 3免缴 4审核未通过）
     */
    @Schema(description = "缴费状态（ 0未缴费 1待审核 2已缴费 3免缴 4审核未通过）")
    private Integer payStatus;

    /**
     * 缴费通知单URL
     */
    @Schema(description = "缴费通知单URL")
    private String payNoticeUrl;

    /**
     * 缴费凭证URL
     */
    @Schema(description = "缴费凭证URL")
    private String payProofUrl;

    /**
     * 缴费驳回原因
     */
    @Schema(description = "缴费驳回原因")
    private String rejectReason;

    /**
     * 缴费提交时间
     */
    @Schema(description = "缴费提交时间")
    private LocalDateTime paySubmitTime;

    /**
     * 考试单价
     */
    @Schema(description = "考试单价")
    private Long examFee;

    /**
     * 班级总缴费
     */
    @Schema(description = "班级总缴费")
    private Long totalPayAmount;

    /**
     * 班级考试人数
     */
    @Schema(description = "班级考试人数")
    private Long candidateCount;
}