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
 * 黑名单相关常量
 */
public final class BlacklistConstants {

    private BlacklistConstants() {
    }

    /** 是否黑名单：否 */
    public static final Boolean NOT_BLACKLIST = false;

    /** 是否黑名单：是 */
    public static final Boolean IS_BLACKLIST = true;

    /** 黑名单时长：无 */
    public static final int DURATION_NONE = 0;

    /** 黑名单时长：1天 */
    public static final int DURATION_1_DAY = 1;

    /** 黑名单时长：1个月 */
    public static final int DURATION_1_MONTH = 2;

    /** 黑名单时长：3个月 */
    public static final int DURATION_3_MONTH = 3;

    /** 黑名单时长：6个月 */
    public static final int DURATION_6_MONTH = 4;

    /** 黑名单时长：1年 */
    public static final int DURATION_1_YEAR = 5;

    /** 黑名单时长：无期限 */
    public static final int DURATION_FOREVER = 6;
}
