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

package top.continew.admin.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.TokenLocalThreadUtil;

/**
 * 请求前置处理器，获取用户在缓存中的登录信息
 *
 * @author Anton
 * @date 2025/3/10-14:42
 */
@ControllerAdvice
@Slf4j
public class UserInfoAdvice {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private String[] PREFIX_FILTER_URI = {"/auth/", "/code/generator/", "/system/message/", "/common/"};

    @ModelAttribute
    public void logRequest(HttpServletRequest request) {
        // 前缀过滤
        String requestURI = request.getRequestURI();
        for (String prefix : PREFIX_FILTER_URI)
            if (requestURI.startsWith(prefix))
                return;

        log.info("收到请求, 存放用户信息: {} {}", request.getMethod(), requestURI);
        String key = RedisConstant.USER_TOKEN + StpUtil.getTokenValue();
        UserTokenDo userInfo = (UserTokenDo)redisTemplate.opsForValue().get(key);
        // 将用户信息存入ThreadLocal中
        TokenLocalThreadUtil.set(userInfo);
    }

}
