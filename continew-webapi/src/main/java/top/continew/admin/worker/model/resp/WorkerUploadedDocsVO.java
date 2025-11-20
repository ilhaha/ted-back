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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/11/4 9:34
 */
@Data
public class WorkerUploadedDocsVO {

    /** 报名ID */
    private Long id;

    /** 考生姓名 */
    private String candidateName;

    /** 资质文件路径 */
    private String qualificationPath;

    /** 资质名称 */
    private String qualificationName;

    /** 身份证正面照片 */
    private String idCardPhotoFront;

    /** 身份证反面照片 */
    private String idCardPhotoBack;

    /** 备注 */
    private String remark;

    /** 人脸照片 */
    private String facePhoto;

    /** 状态 */
    private Integer status;

    /** 材料列表 */
    @JsonIgnore
    private String documents;

    /** 资料列表 */
    private List<WorkerApplyDocumentVO> workerApplyDocuments;
}
