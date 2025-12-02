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

package top.continew.admin.worker.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import top.continew.admin.common.constant.RegexConstants;

import java.io.Serial;
import java.io.Serializable;

/**
 * 作业人员Excel导入数据
 */
@Data
@Schema(description = "用户导入行数据")
public class WorkerImportReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Pattern(regexp = RegexConstants.CHINESE_NAME, message = "姓名长度为 2-5 个简体中文")
    private String candidateName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    private String idCardNumber;

    /**
     * 学历
     */
    @NotBlank(message = "学历不能为空")
    private String education;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话")
    private String phone;

    /**
     * 工作单位
     */
    @NotBlank(message = "工作单位")
    private String workUnit;

    /**
     * 通讯地址
     */
    @NotBlank(message = "通讯地址")
    private String address;

    /**
     * 政治面貌
     */
    @NotBlank(message = "政治面貌")
    private String politicalStatus;
}
