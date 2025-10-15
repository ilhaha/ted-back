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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

@Data
public class EnrollPageResp {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    private String examPlanName;
    /**
     * 考生名称
     */
    @Schema(description = "考生名称")
    private String nickName;
    /**
     * 准考证号
     */
    @Schema(description = "准考证号")
    private String examNumber;
    /**
     * 考场号
     */
    @Schema(description = "考场号")
    private String classroomId;
    /**
     * 座位号
     */
    @Schema(description = "座位号")
    private String seatId;
}
