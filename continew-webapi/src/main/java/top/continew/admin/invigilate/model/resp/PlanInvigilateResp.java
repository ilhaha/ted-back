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

package top.continew.admin.invigilate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试计划监考人员关联信息
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
@Data
@Schema(description = "考试计划监考人员关联信息")
public class PlanInvigilateResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划id
     */
    @Schema(description = "考试计划id")
    private Long examplanId;

    /**
     * 监考人员id
     */
    @Schema(description = "监考人员id")
    private Long invigilatorId;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间")
    private LocalDateTime endTime;

    //    /**
    //     * 监考状态（0：未确认，1：已确认，2：已完成）
    //     */
    //    @Schema(description = "监考状态（0：未确认，1：已确认，2：已完成）")
    //    private Integer invigilateStatus;

    /**
     * 监考状态（0：未监考，1：待录入，2：待审核， 3：已完成）
     */
    @Schema(description = " 监考状态（0：未监考，1：待录入，2：待审核， 3：已完成）")
    private Integer invigilateStatus;

}