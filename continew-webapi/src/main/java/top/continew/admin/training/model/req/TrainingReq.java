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

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 创建或修改培训主表参数
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@Schema(description = "创建或修改培训主表参数")
public class TrainingReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训名称
     */
    @Schema(description = "培训名称")
    @NotBlank(message = "培训名称不能为空")
    @Length(max = 100, message = "培训名称长度不能超过 {max} 个字符")
    private String title;

    /**
     * 视频总时长（秒）
     */
    @Schema(description = "视频总时长（秒）")

    private Integer totalDuration;

    /**
     * 专家ID
     */
    @Schema(description = "专家ID")
    @NotNull(message = "专家ID不能为空")
    private Long expertId;

    /**
     * 专家名字
     */
    @Schema(description = "专家名字")
    //    @NotBlank(message = "专家名字不能为空")
    private String expertName;

//    /**
//     * 费用
//     */
//    @Schema(description = "费用")
//    @NotNull(message = "专家费用不能为空")
//    private BigDecimal fee;

    /**
     * 培训描述
     */
    @Schema(description = "培训描述")
    @Length(max = 65535, message = "培训描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 0-上架 1-下架
     */
    @Schema(description = "0-待审核 1-上架 2-下架")
    private Long status;

    @Schema(description = "封面图")
    @NotNull(message = "专家ID不能为空")
    private String coverPath;

    private LocalDateTime payDeadlineTime; // 时间戳格式

    private BigDecimal expertFee;

    private Long projectId;
}