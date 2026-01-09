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

package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 培训机构班级实体
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@TableName("ted_org_class")
public class OrgClassDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级类型，0作业人员班级，1检验人员班级
     */
    private Integer classType;

    /**
     * 作业人员扫码报考二维码
     */
    private String qrcodeApplyUrl;

    /**
     * 状态，0招生找，1停止招生
     */
    private Integer status;

    /**
     * 资料提交状态
     * 0-未提交 1-已提交
     */
    private Integer docSubmitStatus;

    /**
     * 资料提交时间
     */
    private LocalDateTime docSubmitTime;

    /**
     * 缴费状态（ 0未缴费 1待审核 2已缴费 3免缴 4审核未通过）
     */
    private Integer payStatus;

    /**
     * 缴费提交时间
     */
    private LocalDateTime paySubmitTime;

    /**
     * 缴费通知单URL
     */
    private String payNoticeUrl;

    /**
     * 缴费凭证URL
     */
    private String payProofUrl;

    /**
     * 缴费驳回原因
     */
    private String rejectReason;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}