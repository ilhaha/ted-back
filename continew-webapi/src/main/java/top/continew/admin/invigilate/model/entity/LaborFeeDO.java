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

package top.continew.admin.invigilate.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 考试劳务费配置实体
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Data
@TableName("ted_labor_fee")
public class LaborFeeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 实操考试劳务费单价（元）
     */
    private BigDecimal practicalFee;

    /**
     * 理论考试劳务费单价（元）
     */
    private BigDecimal theoryFee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用：1启用 0禁用
     */
    private Boolean isEnabled;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}