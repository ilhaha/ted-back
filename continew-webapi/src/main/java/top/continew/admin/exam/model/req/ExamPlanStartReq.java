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

package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamPlanStartReq {

    /**
     * 考试计划ID
     */
    @NotNull(message = "未选择考试计划")
    private Long examPlanId;

    /**
     * 开考密码
     */
    @NotBlank(message = "未输入开考密码")
    private String examPassword;

    /**
     * 考场id
     */
    @NotNull(message = "未选择监考考场")
    private Long classroomId;
}