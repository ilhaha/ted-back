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

/**
 * @Author ilhaha
 * @Create 2025/5/14 11:07
 * @Version 1.0
 */
public enum ExamRecordsReviewStatusEnum {

    WAITING_EXAMINATION("待考试", 0), IN_EXAM("考试中", 1), WAITING_RESULTS("等待成绩", 2), COMPLETED("已完成", 3);

    private final String key;
    private final Integer value;

    ExamRecordsReviewStatusEnum(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }
}