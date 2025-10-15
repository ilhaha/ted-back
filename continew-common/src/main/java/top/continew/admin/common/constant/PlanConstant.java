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

package top.continew.admin.common.constant;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author Anton
 * @date 2025/3/12-17:55
 */
@Getter
public enum PlanConstant {

    AUDIT_SUBJECT_APPROVAL_DIRECTOR(1, "待主任审批"), AUDIT_SUPERVISION_APPROVAL(2, "待市监局审批"), SUCCESS(3, "已生效"),
    FAIL(4, "未通过"), EXAM_BEGUN(5, "已开考"), OVER(6, "已结束");

    Integer status;
    String statusString;

    PlanConstant(Integer status, String statusString) {
        this.status = status;
        this.statusString = statusString;
    }

    public static String getStatusString(Integer status) {
        // 遍历枚举值，根据status匹配
        return Arrays.stream(PlanConstant.values())
            .filter(item -> item.getStatus().equals(status))
            .map(PlanConstant::getStatusString)
            .findFirst()
            .orElse("未知状态");
    }
}
