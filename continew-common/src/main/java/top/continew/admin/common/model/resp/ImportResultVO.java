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

package top.continew.admin.common.model.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ImportResultVO<T> {

    /** 可成功导入的数据 */
    private List<T> successList = new ArrayList<>();

    /** 导入失败的数据 */
    private List<ImportFailVO> failList = new ArrayList<>();

    @Data
    public static class ImportFailVO {
        /** Excel 行号 */
        private Integer rowNum;
        /** 原始数据（可选，方便前端回显） */
        private Map<String, Object> rowData;
        /** 失败原因 */
        private String reason;
    }
}
