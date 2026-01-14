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

/**
 * 缴费状态
 * 0-未缴费
 * 1-待审核
 * 2-已缴费
 * 3-免缴
 * 4-审核未通过
 */
public enum OrgClassPayStatusEnum {

    UNPAID(0, "未缴费"), PENDING_AUDIT(1, "待审核"), PAID(2, "已缴费"), FREE(3, "免缴"), AUDIT_REJECTED(4, "审核未通过");

    private final Integer code;
    private final String desc;

    OrgClassPayStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrgClassPayStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrgClassPayStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
