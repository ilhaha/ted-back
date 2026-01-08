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

package top.continew.admin.document.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 资料类型主信息
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Data
@Schema(description = "资料类型主信息")
public class DocumentTypeResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称(如身份证/学历证书)
     */
    @Schema(description = "类型名称(如身份证/学历证书)")
    private String typeName;

    /**
     * 必须上传人员（0全部都需要上传，1京籍上传、2非京籍上传）
     */
    @Schema(description = "必须上传人员（0全部都需要上传，1京籍上传、2非京籍上传）")
    private Integer needUploadPerson;


    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}