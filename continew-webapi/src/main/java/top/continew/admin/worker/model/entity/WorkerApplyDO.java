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

package top.continew.admin.worker.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 作业人员报名实体
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@TableName("ted_worker_apply")
public class WorkerApplyDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 作业人员姓名
     */
    private String candidateName;

    /**
     * 作业人员性别
     */
    private String gender;

    /**
     * 作业人员手机号
     */
    private String phone;

    /**
     * 报名资格申请表路径
     */
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    private String qualificationName;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    private String facePhoto;

    /**
     * 报名方式，0作业人员自报名，1机构批量导入
     */
    private Integer applyType;

    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    private String remark;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}