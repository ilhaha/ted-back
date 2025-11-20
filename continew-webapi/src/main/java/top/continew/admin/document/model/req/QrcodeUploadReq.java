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

package top.continew.admin.document.model.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import top.continew.admin.document.model.dto.DocFileDTO;

import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/22 19:35
 *
 *         考试通过二维码上传资料
 */
@Data
public class QrcodeUploadReq {

    /**
     * 考生id
     */
    @NotBlank(message = "二维码错误")
    private String candidateId;

    /**
     * 计划id
     */
    @NotBlank(message = "二维码错误")
    private String planId;

    /**
     * 身份证后六位
     */
    @NotBlank(message = "身份证后六位未填写")
    private String idLastSix;

    /**
     * 资料申请表文件集合
     */
    private List<DocFileDTO> docFileList;

    /**
     * 资料申请表文件
     */
    private String qualificationFileUrl;
}
