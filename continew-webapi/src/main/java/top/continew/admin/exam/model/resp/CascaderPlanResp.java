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

package top.continew.admin.exam.model.resp;

import lombok.Data;
import java.util.List;

/**
 * 项目-考试计划级联选择器响应 DTO
 */
@Data
public class CascaderPlanResp {

    /**
     * 项目ID 或考试计划ID
     */
    private Long value;

    /**
     * 显示名称（项目名称或考试计划名称）
     */
    private String label;

    /**
     * 子级考试计划列表
     */
    private List<CascaderPlanResp> children;
}
