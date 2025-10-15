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

package top.continew.admin.certificate.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生证件查询条件
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Data
@Schema(description = "考生证件查询条件")
public class CandidateCertificateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 考生名称
     */
    @Schema(description = "证件名称")
    @TableField(exist = false)
    @QueryIgnore
    private String certificateName;
    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @TableField(exist = false)
    private String projectName;
    /**
     * 证件状态，0:未持有;1:持有;2:到期;3:待换证
     */
    @Schema(description = "证件状态，0:未持有;1:持有未到期;2:持有已到期;")
    @Query(type = QueryType.EQ)
    private Integer certificateStatus;

    /**
     * 证件编号
     */
    @Schema(description = "证件编号")
    @Query(type = QueryType.EQ)
    private String certificateNumber;

}