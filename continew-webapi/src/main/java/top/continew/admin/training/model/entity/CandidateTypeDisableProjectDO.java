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
 * 考生类型与禁考项目关联实体
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Data
@TableName("ted_candidate_type_disable_project")
public class CandidateTypeDisableProjectDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生类型ID
     */
    private Long candidateTypeId;

    /**
     * 禁考项目ID
     */
    private Long disableProjectId;

    /**
     * 删除标记
     */
    private Integer isDeleted;
}