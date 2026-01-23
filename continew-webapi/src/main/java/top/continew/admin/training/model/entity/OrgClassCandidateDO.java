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
 * 机构班级与考生关联表实体
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Data
@TableName("ted_org_class_candidate")
public class OrgClassCandidateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 删除标记
     */
    private Integer isDeleted;

    /**
     * 状态：0 正在班级 1 已考试
     */
    private Integer status;
}