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

package top.continew.admin.worker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ilhaha
 * @Create 2025/12/3 9:56
 */
@Data
public class UploadGroupDTO {
    private MultipartFile idCardFront;
    private MultipartFile idCardBack;
    private MultipartFile photoOneInch;
    private MultipartFile applyForm;

    // 每个 docId 对应一个文件 + 名称
    private Map<Long, ProjectDocItem> projectDocs = new HashMap<>();

    @Data
    @AllArgsConstructor
    public static class ProjectDocItem {
        private MultipartFile file;
        private String name;   // 资料名称
    }
}
