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

import java.time.LocalDateTime;

/**
 * @author Anton
 * @date 2025/4/25-10:13
 */

@Data
public class InvigilateExamDetailResp {

    private String examPlanName;
    private String imageUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String redeme;
    private String locationName;
    private Integer invigilateStatus;
    private Long examDuration;
}
