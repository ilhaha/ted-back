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

package top.continew.admin.constant;

public final class SmsConstants {

    private SmsConstants() {
        // 防止实例化
    }

    // ==================== 短信类型 ====================
    public static final String REGISTRATION_TEMPLATE = "registerCode";
    public static final String LOGIN_VERIFICATION_TEMPLATE = "loginCode";
    public static final String EXAM_NOTIFICATION_TEMPLATE = "examCode";
    public static final String ENROLLMENT_CONFIRMATION_TEMPLATE = "enrollNotice";
    public static final String DEVICE_BINDING_TEMPLATE = "phoneConnectedCode";
}
