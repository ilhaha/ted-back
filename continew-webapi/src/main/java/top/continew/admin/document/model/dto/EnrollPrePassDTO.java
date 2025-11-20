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

package top.continew.admin.document.model.dto;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/10/29 19:55
 */
@Data
public class EnrollPrePassDTO {

    /** 报名预上传记录 ID */
    private Long id;

    /** 考生 ID */
    private Long candidatesId;

    /** 考试计划 ID */
    private Long planId;

    /** 报名资格申请表路径 */
    private String qualificationFileUrl;

    /** 批次 ID */
    private Long batchId;

    /** 文件类型 ID */
    private Long typeId;

    /** 文件路径（逗号拼接） */
    private String docPaths;

}
