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
 * 资料提交状态枚举
 * 0-未提交 1-已提交 2-审核中 3-已通过 4-已驳回
 */
public enum OrgClassDocSubmitStatusEnum {

    NOT_SUBMITTED(0, "未提交"), SUBMITTED(1, "已提交");

    private final Integer code;
    private final String desc;

    OrgClassDocSubmitStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrgClassDocSubmitStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrgClassDocSubmitStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
