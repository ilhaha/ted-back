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

package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/17 17:54
 */
@Getter
@RequiredArgsConstructor
public enum ExamViolationTypeEnum {

    NO_SWITCH(0, "无违规"),

    SCREEN_SWITCH(1, "【考试违规-切屏】考生累计切换非考试窗口/应用达 3 次");

    private final Integer value;

    private final String description;

    /**
     * 根据 value 获取 description
     * 
     * @param value 枚举值
     * @return description，如果找不到返回 null
     */
    public static String getDescriptionByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ExamViolationTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type.getDescription();
            }
        }
        return null;
    }
}
