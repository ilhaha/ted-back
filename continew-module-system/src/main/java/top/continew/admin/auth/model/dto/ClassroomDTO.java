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

package top.continew.admin.auth.model.dto;

import lombok.Data;

/**
 * @Author ilhaha
 * @Create 2025/5/27 09:10
 * @Version 1.0
 */
@Data
public class ClassroomDTO {

    /**
     * 考场ID
     */
    private Long classroomId;

    /**
     * 考场名称
     */
    private String classroomName;
}
