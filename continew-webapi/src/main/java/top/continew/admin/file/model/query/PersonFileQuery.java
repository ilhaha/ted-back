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

package top.continew.admin.generator.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.QueryIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 个人档案管理查询条件
 *
 * @author Anton
 * @since 2025/04/23 15:23
 */
@Data
@Schema(description = "个人档案管理查询条件")
public class PersonFileQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Schema(description = "考试计划名称")
    @QueryIgnore
    private String planName;

    /**
     *
     */
    @Schema(description = "考生名称")
    @QueryIgnore
    private String nickName;
}