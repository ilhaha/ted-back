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

package top.continew.admin.common.util;

import top.continew.admin.common.model.entity.UserTokenDo;

/**
 * @author Anton
 * @date 2025/3/6-14:42
 */
public class TokenLocalThreadUtil {

    private static ThreadLocal<UserTokenDo> threadLocal = new ThreadLocal<UserTokenDo>();

    private TokenLocalThreadUtil() {
    }

    public static void set(UserTokenDo userTokenDto) {
        threadLocal.set(userTokenDto);
    }

    public static UserTokenDo get() {
        return threadLocal.get();
    }

}
