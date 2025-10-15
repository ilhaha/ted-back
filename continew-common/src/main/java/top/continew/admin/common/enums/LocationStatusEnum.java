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

package top.continew.admin.common.enums;

import lombok.Getter;

/**
 * @author Anton
 * @date 2025/3/14-10:01
 */
@Getter
public enum LocationStatusEnum {

    // 运营中
    RUNNING(0, "运营中"), RESTING(1, "休息中"), MAINTENANCE(2, "维护中"), STOPPED(3, "停止运营");

    int status;
    String statusStr;

    LocationStatusEnum(int status, String statusStr) {
        this.status = status;
        this.statusStr = statusStr;
    }

    public static String getStatusStrByCode(int status) {
        for (LocationStatusEnum e : values()) {
            if (e.getStatus() == status) {
                return e.getStatusStr();
            }
        }
        return "未知状态";
    }

}
