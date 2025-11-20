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

package top.continew.admin.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author ilhaha
 * @Create 2025/11/5 13:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelUploadFileResultDTO {

    /** OSS 地址（普通图片） */
    private String docUrl;

    /** 识别出的姓名 */
    private String realName;

    /** 识别出的性别 */
    private String gender;

    /** 识别出的身份证号码 */
    private String idCardNumber;

    /** 身份证正面照 URL */
    private String idCardPhotoFront;

    /** 身份证反面照 URL */
    private String idCardPhotoBack;

    /** 一寸照或人脸照 URL */
    private String facePhoto;

    /**
     * 有效期截止日期
     */
    private LocalDate validEndDate;

}
