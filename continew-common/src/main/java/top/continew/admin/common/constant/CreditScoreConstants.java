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
 * 信誉分阈值常量
 */
public final class CreditScoreConstants {

    private CreditScoreConstants() {
    }

    /** 优秀 */
    public static final int EXCELLENT = 90;

    /** 良好 */
    public static final int GOOD = 80;

    /** 及格线 */
    public static final int PASS = 70;

    /** 最低分 */
    public static final int MIN = 0;

    /** 最高分 */
    public static final int MAX = 100;

    /**
     * 根据信誉分计算报名顺延天数
     */
    public static int calcDelayDays(int creditScore) {
        if (creditScore >= CreditScoreConstants.EXCELLENT) {
            return 0;
        }
        if (creditScore >= CreditScoreConstants.GOOD) {
            return 1;
        }
        if (creditScore >= CreditScoreConstants.PASS) {
            return 2;
        }
        return -1; // 不允许报名
    }

}
