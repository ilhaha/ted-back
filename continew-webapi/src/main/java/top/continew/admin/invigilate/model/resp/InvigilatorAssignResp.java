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

package top.continew.admin.invigilate.model.resp;

import lombok.Data;

@Data
public class InvigilatorAssignResp {

    /**
     * id
     */
    private Long id;

    /**
     * 监考员昵称
     */
    private String nickname;

    /**
     * 监考员 ID
     */
    private Long invigilatorId;

    /**
     * 考场 ID
     */
    private Long classroomId;

    /**
     * 考场名称
     */
    private String classroomName;

    /**
     * 监考状态
     */
    private Integer invigilateStatus;

    /**
     * 监考密码
     */
    private String examPassword;

    /**
     * 考场考试类型，0理论考试，1实操考试
     */
    private Integer examType;

    /**
     * 考点地址
     */
    private String detailedAddress;

    /**
     * 考点名称
     */
    private String locationName;
}
