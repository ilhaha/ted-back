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
 * @Create 2025/5/26 16:56
 * @Version 1.0
 */
public class EnrollStatusConstant {

    /**
     * 报名状态：未报名
     */
    public static final Integer NOT_REGISTERED = 0;
    /**
     * 报名状态：已报名
     */
    public static final Integer SIGNED_UP = 1;

    /**
     * 报名状态：已完成
     */
    public static final Integer COMPLETED = 2;

    /**
     * 报名状态：已过期
     */
    public static final Integer EXPIRED = 3;

    /**
     * 报名状态：审核中
     */
    public static final Integer UNDER_REVIEW = 4;

    /**
     * 考试状态：未签到
     */
    public static final Integer NOT_SIGNED_IN = 0;

    /**
     * 考试状态：已签到
     */
    public static final Integer SIGNED_IN = 1;


    /**
     * 考试状态：正在考试
     */
    public static final Integer IN_PROGRESS = 4;

    /**
     * 考试状态：已交卷
     */
    public static final Integer SUBMITTED = 2;

    /**
     * 考试状态：缺考
     */
    public static final Integer ABSENT = 3;

}
