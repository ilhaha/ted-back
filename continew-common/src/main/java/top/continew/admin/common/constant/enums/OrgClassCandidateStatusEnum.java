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

/**
 * 班级学员状态枚举
 */
@Getter
public enum OrgClassCandidateStatusEnum {

    /**
     * 正在班级
     */
    IN_CLASS(0, "正在班级"),

    /**
     * 已考试
     */
    EXAMINED(1, "已考试");

    private final Integer value;
    private final String desc;

    OrgClassCandidateStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
