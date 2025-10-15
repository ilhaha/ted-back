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

public enum ReviewStatus {

    /** 待审核 */
    PENDING(0, "待审核"),

    /** 已审核 */
    APPROVED(1, "已审核"),

    /** 审核不通过 */
    REJECTED(2, "审核不通过");

    private final int code;
    private final String description;

    ReviewStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举实例
     */
    public static ReviewStatus fromCode(int code) {
        for (ReviewStatus status : ReviewStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid review status code: " + code);
    }

    /**
     * 判断是否是有效状态码
     */
    public static boolean isValidCode(int code) {
        for (ReviewStatus status : ReviewStatus.values()) {
            if (status.code == code) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.description;
    }
}