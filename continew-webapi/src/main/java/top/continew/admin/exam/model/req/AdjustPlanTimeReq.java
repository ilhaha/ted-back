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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建或修改考试计划参数
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@Schema(description = "创建或修改考试计划参数")
public class AdjustPlanTimeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试开始时间
     */
    @NotNull(message = "考试开始时间不能为空")
    //    @Future(message = "考试开始时间必须是未来时间")
    private LocalDateTime startTime;

    @Schema(description = "报名开始时间范围")
    @Size(min = 2, max = 2, message = "报名开始时间范围必须包含 2 个时间点")
    private List<@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "报名开始时间格式不正确") String> enrollList;


    /**
     * 准考证下载截至时间
     */
//    @NotNull(message = "准考证下载截至时间不能为空")
    private LocalDateTime admitCardEndTime;

}