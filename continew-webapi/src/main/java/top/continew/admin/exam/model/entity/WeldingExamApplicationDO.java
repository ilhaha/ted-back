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

package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 机构申请焊接考试项目实体
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Data
@TableName("ted_welding_exam_application")
public class WeldingExamApplicationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请机构ID
     */
    private Long orgId;

    /**
     * 焊接类型：0-金属焊接，1-非金属焊接
     */
    private Integer weldingType;

    /**
     * 焊接考试项目名称
     */
    private String projectName;

    /**
     * 考试项目代码
     */
    private String projectCode;

    /**
     * 申请原因或说明
     */
    private String applicationReason;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer status;

    /**
     * 审核意见/备注
     */
    private String reviewComment;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
}