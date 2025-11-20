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

package top.continew.admin.training.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.continew.admin.worker.model.entity.WorkerApplyDO;

import java.util.Map;

/**
 * @author ilhaha
 * @Create 2025/11/5 16:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedSuccessVO extends WorkerApplyDO {

    private String excelName;

    private Integer rowNum;

    private Map<String, String> docMap;

    private Boolean isUpload;

    /**
     * aesPhone
     */
    private String encFieldA;

    /**
     * aesIdCard
     */
    private String encFieldB;
}
