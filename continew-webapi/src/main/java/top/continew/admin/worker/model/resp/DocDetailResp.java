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

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2026/1/7 17:25
 */
@Data
public class DocDetailResp implements Serializable {

    /**
     * 报名资格申请表路径
     */
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    private String qualificationName;

    /**
     * 身份证住址
     */
    private String idCardAddress;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    private String facePhoto;

    // 资料集合
    private List<ProjectNeedUploadDocVO> uploadedDocs;
}
