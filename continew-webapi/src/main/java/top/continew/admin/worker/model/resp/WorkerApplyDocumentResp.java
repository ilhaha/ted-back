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

package top.continew.admin.worker.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 作业人员报名上传的资料信息
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Data
@Schema(description = "作业人员报名上传的资料信息")
public class WorkerApplyDocumentResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业人员报名表ID
     */
    @Schema(description = "作业人员报名表ID")
    private Long workerApplyId;

    /**
     * 资料存储路径
     */
    @Schema(description = "资料存储路径")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    private Long typeId;

    /**
     * 资料类型
     */
    @Schema(description = "资料类型")
    private String typeName;
}