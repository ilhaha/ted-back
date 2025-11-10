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

package top.continew.admin.training.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 机构预报名信息参数
 *
 * @author ilhaha
 * @since 2025/04/07 10:53
 */
@Data
@Schema(description = "机构预报名信息参数")
public class OrgApplyReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 计划id
     */
    @Schema(description = "计划id")
    @NotNull(message = "报考计划为空")
    private Long examPlanId;


    /**
     * 预报名考生名单
     */
    @NotEmpty(message = "预报名考生名单")
    private List<List<Long>> candidateIds;


}