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

package top.continew.admin.auth.model.resp;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author ilhaha
 * @Create 2025/10/20 11:58
 */
@Data
public class CandidatesIdCardVO {
    /**
     * 姓名
     */
    private String realName;

    /**
     * 性别（男 女）
     */
    private String gender;

    /**
     * 民族
     */
    private String nation;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 住址
     */
    private String address;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 签发机关
     */
    private String issuingAuthority;

    /**
     * 有效期开始日期
     */
    private LocalDate validStartDate;

    /**
     * 有效期截止日期
     */
    private LocalDate validEndDate;

    /**
     * 身份证正面照片路径
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    private String idCardPhotoBack;

    /**
     * 逻辑删除标志：0否 1是
     */
    private Integer isDeleted;
}
