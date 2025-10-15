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

package top.continew.admin.exam.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SpecialCertificationApplicantListReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String studentMapStr;// 考生id url路径

    private String planId;// 考试计划id

}
