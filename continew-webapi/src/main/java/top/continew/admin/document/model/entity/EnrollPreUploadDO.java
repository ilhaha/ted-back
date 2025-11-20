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

package top.continew.admin.document.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 机构报考-考生扫码上传文件实体
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@TableName("ted_enroll_pre_upload")
public class EnrollPreUploadDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    private Long candidatesId;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 机构班级id
     */
    private Long batchId;

    /**
     * 报名资格申请表URL
     */
    private String qualificationFileUrl;

    /**
     * 审核状态（0未审核，1审核通过，2退回补正，3虚假资料-禁止再次申报项目）
     */
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    private String remark;

    /**
     * 是否删除（0否，1是）
     */
    private Boolean isDeleted;
}