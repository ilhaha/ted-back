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

package top.continew.admin.controller.auth;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.constant.enums.CandidateTypeEnum;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.query.UserQuery;
import top.continew.admin.system.model.req.user.UserReq;
import top.continew.admin.system.model.resp.user.UserDetailResp;
import top.continew.admin.system.model.resp.user.UserResp;
import top.continew.admin.system.service.UserService;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.extension.crud.model.resp.BaseIdResp;

import java.util.List;

/**
 * 注册考生账号
 *
 */

@Validated
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/auth/login/identity", api = {Api.PAGE, Api.LIST, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class LoginStudentController extends BaseController<UserService, UserResp, UserDetailResp, UserQuery, UserReq> {

    @Value("${examine.userRole.candidatesId}")
    private Long candidatesId;// 考生id

    @Value("${examine.deptId.examCenterId}")
    private Long examCenterId;// 部门id

    private final UserService userService;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private CandidateTypeMapper candidateTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseIdResp<Long> add(UserReq req) {
        String rawUsername = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getUsername()));
        String username = aesWithHMAC.encryptAndSign(rawUsername);
        UserDO userDB = userService.getByUsername(username);
        ValidationUtils.throwIfNotNull(userDB, "用户已存在");
        String rawPhone = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getPhone()));
        boolean phoneCount = userService.isPhoneExists(rawPhone, null);
        ValidationUtils.throwIf(phoneCount, "绑定失败，手机号已被绑定");
        req.setRoleIds(List.of(candidatesId));
        req.setDeptId(examCenterId);
        req.setNickname(req.getNickname());
        req.setPhone(rawPhone);
        req.setUsername(username);
        String rawPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getPassword()));
        ValidationUtils.throwIfNull(rawPassword, "密码解密失败");
        if (rawPassword == null || rawPassword.isEmpty())
            return null;
        req.setPassword(rawPassword);
        BaseIdResp<Long> res = super.add(req);
        CandidateTypeDO candidateTypeDO = new CandidateTypeDO();
        candidateTypeDO.setCandidateId(res.getId());
        candidateTypeDO.setType(CandidateTypeEnum.INSPECTION.getValue());
        candidateTypeMapper.insert(candidateTypeDO);
        return res;
    }

}
