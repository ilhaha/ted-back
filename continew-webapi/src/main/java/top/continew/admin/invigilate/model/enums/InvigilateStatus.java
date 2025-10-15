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

package top.continew.admin.invigilate.model.enums;

public enum InvigilateStatus {

    NOT_INVIGILATED(0, "未监考"), PENDING_ENTRY(1, "待录入"), PENDING_REVIEW(2, "待审核"), COMPLETED(3, "已完成");

    private final long code;
    private final String description;

    InvigilateStatus(long code, String description) {
        this.code = code;
        this.description = description;
    }

    public long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取对应的枚举实例
     * 
     * @param code 状态码
     * @return 对应的枚举实例，未找到时返回 null
     */
    public static InvigilateStatus fromCode(long code) {
        for (InvigilateStatus status : InvigilateStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null; // 或抛出 IllegalArgumentException
    }

    /**
     * 可选：根据业务需求重写 toString()
     */
    @Override
    public String toString() {
        return description;
    }
}