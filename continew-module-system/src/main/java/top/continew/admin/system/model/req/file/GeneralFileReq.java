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

package top.continew.admin.system.model.req.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author ilhaha
 * @Create 2025/3/24 16:49
 * @Version 1.0
 */
@Data
@Schema(description = "上传文件参数")
public class GeneralFileReq implements Serializable {

    /**
     * 上传的文件类型（固定值pic、head、certificate、video）
     */
    private String type;

    /**
     * 身份证正反面 1正面 0 反面
     */
    private Boolean idCardFrontOrBack;
}
