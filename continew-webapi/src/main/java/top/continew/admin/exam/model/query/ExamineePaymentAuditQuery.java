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

package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生缴费审核查询条件
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@Schema(description = "考生缴费审核查询条件")
public class ExamineePaymentAuditQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联考试计划ID
     */
    @Schema(description = "关联考试计划ID")
    @Query(type = QueryType.EQ)
    private Long examPlanId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long examineeId;

    /**
     * 作业人员班级id
     */
    @Schema(description = "作业人员班级id")
    @Query(type = QueryType.EQ, columns = "tepa.class_id")
    private Long classId;

    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号")
    @Query(type = QueryType.EQ)
    private String noticeNo;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回
     */
    @Schema(description = "审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回 ")
    @Query(type = QueryType.EQ)
    private Integer auditStatus;

    @Schema(description = "考生姓名查询")
    @Query(type = QueryType.LIKE, columns = "su.nickname")
    private String examineeName; // 考生姓名查询

    @Schema(description = "考试计划名称查询")
    @Query(type = QueryType.LIKE, columns = "tep.exam_plan_name")
    private String planName; // 考试计划名称查询

    @Schema(description = "是否是审核作业人员缴费")
    @QueryIgnore
    private Boolean isWorker;

}