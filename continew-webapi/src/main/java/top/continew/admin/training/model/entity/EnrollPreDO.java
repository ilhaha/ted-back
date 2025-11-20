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

/**
 * 机构考生预报名实体
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Data
@TableName("ted_enroll_pre")
public class EnrollPreDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 上传资料二维码
     */
    private String uploadQrcode;

    /**
     * 资料上传状态 0-资料待补充 1-报考资料已齐全
     */
    private Integer status;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}