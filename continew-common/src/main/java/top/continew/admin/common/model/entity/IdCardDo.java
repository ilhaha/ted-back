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

package top.continew.admin.common.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 类说明:
 *
 * @author 丶Anton
 * @email itanton666@gmail.com
 * @date 2025/3/24 10:22
 */
@Data
@ToString
public class IdCardDo {

    /**
     * 姓名
     */
    private String name;
    /**
     * 性别，男0，女1
     */
    private Boolean sex;
    /**
     * 民族
     */
    private String ethnicity;
    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    /**
     * 身份证地址
     */
    private String address;
    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 签证机关
     */
    private String IssueAuthority;

    /**
     * 证件有效期开始时间
     */
    private LocalDate setValidPeriodStart;

    /**
     * 证件有效期结束时间
     */
    private LocalDate setValidPeriodEnd;

}
