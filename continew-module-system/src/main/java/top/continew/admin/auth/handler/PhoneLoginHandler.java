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

package top.continew.admin.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.continew.admin.auth.AbstractLoginHandler;
import top.continew.admin.auth.enums.AuthTypeEnum;
import top.continew.admin.auth.model.req.PhoneLoginReq;
import top.continew.admin.auth.model.resp.LoginResp;
import top.continew.admin.common.constant.CacheConstants;
import top.continew.admin.common.constant.RedisConstant;
import top.continew.admin.common.model.entity.UserRoleDeptDo;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.resp.ClientResp;
import top.continew.starter.cache.redisson.util.RedisUtils;
import top.continew.starter.core.validation.ValidationUtils;

import java.util.concurrent.TimeUnit;

/**
 * 手机号登录处理器
 *
 * @author KAI
 * @author Charles7c
 * @since 2024/12/22 14:59
 */
@Component
public class PhoneLoginHandler extends AbstractLoginHandler<PhoneLoginReq> {

    @Value("${examine.userRole.candidatesId}")
    private Long candidatesId;

    @Value("${examine.userRole.invigilatorId}")
    private Long invigilatorId;

    @Value("${examine.userRole.organizationId}")
    private Long organizationId;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResp login(PhoneLoginReq req, ClientResp client, HttpServletRequest request) {
        // 验证手机号
        UserDO user = userService.getByPhone(req.getPhone());
        ValidationUtils.throwIfNull(user, "此手机号未绑定本系统账号");
        // 检查用户状态
        super.checkUserStatus(user);
        // 执行认证
        String token = super.authenticate(user, client);
        //开始修改
        UserTokenDo userTokenDto = new UserTokenDo();
        BeanUtils.copyProperties(user, userTokenDto);
        userTokenDto.setToken(token);
        // 查询用户角色、用户部门
        UserRoleDeptDo userDo = userService.getUserRoleDeptByUserId(user.getId());
        BeanUtils.copyProperties(userDo, userTokenDto);
        //        userTokenDto.setRoleId(userDo.getRoleId());
        //        userTokenDto.setDeptId(userDo.getDeptId());
        //        userTokenDto.setRoleName(userDo.getRoleName());
        //        userTokenDto.setUserId(user.getId());
        //        userTokenDto.setParentDeptId(userDo.getParentDeptId());
        // 将认证保存至Redis中
        redisTemplate.opsForValue()
            .set(RedisConstant.USER_TOKEN + token, userTokenDto, RedisConstant.FIFTEEN_DAYS, TimeUnit.SECONDS);

        Long roldId = userDo.getRoleId();
        String id = null;//返回给前端做路由用的
        if (roldId.equals(candidatesId)) {
            id = String.valueOf(1);
        } else if (roldId.equals(invigilatorId)) {
            id = String.valueOf(2);
        } else if (roldId.equals(organizationId)) {
            id = String.valueOf(3);
            boolean b = userService.checkOrg(user.getId());
            ValidationUtils.throwIf(!b, "你登陆的机构账号未绑定机构！请联系管理员绑定机构");
        }
        return LoginResp.builder().token(token).role(id).build();
    }

    @Override
    public void preLogin(PhoneLoginReq req, ClientResp client, HttpServletRequest request) {
        String phone = req.getPhone();
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + phone;
        String captcha = RedisUtils.get(captchaKey);
        ValidationUtils.throwIfBlank(captcha, CAPTCHA_EXPIRED);
        ValidationUtils.throwIfNotEqualIgnoreCase(req.getCaptcha(), captcha, CAPTCHA_ERROR);
        RedisUtils.delete(captchaKey);
    }

    @Override
    public AuthTypeEnum getAuthType() {
        return AuthTypeEnum.PHONE;
    }

    @Override
    public LoginResp candidatesOrInviteLogin(PhoneLoginReq req, ClientResp client, HttpServletRequest request) {
        return null;
    }

    @Override
    public LoginResp examLogin(PhoneLoginReq req, ClientResp client, HttpServletRequest request) {
        return null;
    }

    @Override
    public LoginResp invigilatorLogin(PhoneLoginReq req, ClientResp client, HttpServletRequest request) {
        return null;
    }

}