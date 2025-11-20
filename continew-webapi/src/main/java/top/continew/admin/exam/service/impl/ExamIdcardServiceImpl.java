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

package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.service.UserService;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamIdcardMapper;
import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.exam.model.query.ExamIdcardQuery;
import top.continew.admin.exam.model.req.ExamIdcardReq;
import top.continew.admin.exam.model.resp.ExamIdcardDetailResp;
import top.continew.admin.exam.model.resp.ExamIdcardResp;
import top.continew.admin.exam.service.ExamIdcardService;

import java.time.LocalDate;

/**
 * 考生身份证信息业务实现
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Service
@RequiredArgsConstructor
public class ExamIdcardServiceImpl extends BaseServiceImpl<ExamIdcardMapper, ExamIdcardDO, ExamIdcardResp, ExamIdcardDetailResp, ExamIdcardQuery, ExamIdcardReq> implements ExamIdcardService {

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private UserService userService;

    /**
     * 考生根据身份证号查看是否已实名
     * 
     * @param username
     * @return
     */
    @Override
    public Boolean verifyRealName(String username) {
        String decrUsername = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(username));
        ValidationUtils.throwIfBlank(username, "用户名解密失败");
        String aesUsername = aesWithHMAC.encryptAndSign(decrUsername);
        UserDO userDO = userService.getByUsername(aesUsername);
        ValidationUtils.throwIf(userService.isWorker(userDO.getId()), "用户名或密码错误");
        LambdaQueryWrapper<ExamIdcardDO> examIdcardDOLambdaQueryWrapper = new LambdaQueryWrapper<ExamIdcardDO>()
            .eq(ExamIdcardDO::getIdCardNumber, aesUsername);
        return baseMapper.selectCount(examIdcardDOLambdaQueryWrapper) > 0;
    }

    @Override
    public Long saveRealName(ExamIdcardReq examIdcardReq) {
        // 校验有效期是否过期
        LocalDate validEndDate = examIdcardReq.getValidEndDate();
        LocalDate today = LocalDate.now();
        ValidationUtils.throwIf(validEndDate != null && today.isAfter(validEndDate), "身份证已过期");

        //加密身份证号
        String encryptedIdCardNumber = aesWithHMAC.encryptAndSign(examIdcardReq.getIdCardNumber());

        // 校验身份证号是否已实名
        LambdaQueryWrapper<ExamIdcardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamIdcardDO::getIdCardNumber, encryptedIdCardNumber)
            .eq(ExamIdcardDO::getIsDeleted, 0)
            .select(ExamIdcardDO::getIdCardNumber);
        Long count = baseMapper.selectCount(queryWrapper);
        ValidationUtils.throwIf(count > 0, "该身份证号码已实名，不能重复认证");
        //加密再保存
        examIdcardReq.setIdCardNumber(encryptedIdCardNumber);
        return super.add(examIdcardReq);
    }
}