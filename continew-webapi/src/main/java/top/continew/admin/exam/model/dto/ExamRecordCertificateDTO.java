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

package top.continew.admin.exam.model.dto;

import lombok.Data;

@Data
public class ExamRecordCertificateDTO {

    /**
     * 考试记录ID
     */
    private Long id;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 考生昵称
     */
    private String nickname;

    /**
     * 工作区域
     */
    private String address;


    /**
     * 用户名 / 身份证号
     */
    private String username;

    /**
     * 报名用户ID
     */
    private Long userId;

    /**
     * 工作单位
     */
    private String workUnit;

    /**
     * 一寸免冠照
     */
    private String facePhoto;

    /**
     * 项目分类名称
     */
    private String categoryName;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 项目id
     */
    private Long projectId;
}
