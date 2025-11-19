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

package top.continew.admin.system.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvigilatorTimeVO {

    private long planId;// 计划id

    private String planName;// 考试计划名称

    private long classroomId;// 考场id

    private String classroomName;// 考场名称

    private LocalDateTime startTime;// 监考时间开始时间

    private LocalDateTime endTime;// 监考时间结束时间

}
