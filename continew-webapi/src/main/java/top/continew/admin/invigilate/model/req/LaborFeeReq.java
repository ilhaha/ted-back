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

package top.continew.admin.invigilate.model.req;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 创建或修改考试劳务费配置参数
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Data
@Schema(description = "创建或修改考试劳务费配置参数")
public class LaborFeeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 实操考试劳务费单价（元）
     */
    @Schema(description = "实操考试劳务费单价（元）")
    private BigDecimal practicalFee;
    /**
     * 理论考试劳务费单价（元）
     */
    @Schema(description = "理论考试劳务费单价（元）")
    private BigDecimal theoryFee;
    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
    /**
     * 是否启用：1启用 0禁用
     */
    @Schema(description = "是否启用：1启用 0禁用")
    private Boolean isEnabled;
}