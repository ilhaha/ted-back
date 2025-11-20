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

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改机构与八大类关联，记录多对多关系参数
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Data
@Schema(description = "创建或修改机构与八大类关联，记录多对多关系参数")
public class OrgCategoryRelationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID，关联ted_org表的id
     */
    @Schema(description = "机构ID，关联ted_org表的id")
    @NotNull(message = "机构ID，关联ted_org表的id不能为空")
    private Long orgId;

    /**
     * 八大类ID，关联ted_category表的id
     */
    @Schema(description = "八大类ID，关联ted_category表的id")
    @NotNull(message = "八大类ID，关联ted_category表的id不能为空")
    private Long categoryId;
}