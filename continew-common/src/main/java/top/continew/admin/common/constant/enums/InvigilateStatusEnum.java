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

@Getter
public enum InvigilateStatusEnum {

    NOT_START(0, "待监考"), TO_FILL(1, "待录入"), TO_AUDIT(2, "待审核"), FINISHED(3, "已完成"), TO_CONFIRM(4, "待监考员确认"),
    REJECTED(5, "监考员拒绝监考");

    private final Integer value;
    private final String description;

    InvigilateStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
