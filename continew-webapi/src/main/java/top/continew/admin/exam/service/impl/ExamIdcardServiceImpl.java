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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.enums.EducationVerifyStatusEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.config.EducationConfig;
import top.continew.admin.exam.model.req.AuditExamIdCardReq;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.resp.user.UserDetailResp;
import top.continew.admin.system.service.UserService;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamIdcardMapper;
import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.exam.model.query.ExamIdcardQuery;
import top.continew.admin.exam.model.req.ExamIdcardReq;
import top.continew.admin.exam.model.resp.ExamIdcardDetailResp;
import top.continew.admin.exam.model.resp.ExamIdcardResp;
import top.continew.admin.exam.service.ExamIdcardService;

import java.time.LocalDateTime;
import java.util.List;

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

    private final EducationConfig educationConfig;

    /**
     * 重写page
     * 
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamIdcardResp> page(ExamIdcardQuery query, PageQuery pageQuery) {
        String idCardNumber = query.getIdCardNumber();
        if (StrUtil.isNotBlank(idCardNumber)) {
            query.setIdCardNumber(aesWithHMAC.encryptAndSign(idCardNumber));
        }
        PageResp<ExamIdcardResp> page = super.page(query, pageQuery);
        List<ExamIdcardResp> list = page.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            page.setList(list.stream().map(item -> {
                item.setIdCardNumber(aesWithHMAC.verifyAndDecrypt(item.getIdCardNumber()));
                return item;
            }).toList());
        }
        return page;
    }

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
    @Transactional(rollbackFor = Exception.class)
    public Long saveRealName(ExamIdcardReq examIdcardReq) {
        //加密身份证号
        String encryptedIdCardNumber = aesWithHMAC.encryptAndSign(examIdcardReq.getIdCardNumber());

        //先查用户表判断是否已经注册过
        UserDO userDO = userService.getByUsername(encryptedIdCardNumber);
        ValidationUtils.throwIfNull(userDO, "该身份证未注册,无法进行实名操作");

        // 校验身份证号是否已实名
        LambdaQueryWrapper<ExamIdcardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamIdcardDO::getIdCardNumber, encryptedIdCardNumber).select(ExamIdcardDO::getIdCardNumber);
        Long count = baseMapper.selectCount(queryWrapper);
        ValidationUtils.throwIf(count > 0, "该身份证号码已实名，不能重复认证");
        //加密再保存
        examIdcardReq.setIdCardNumber(encryptedIdCardNumber);
        // 如果有新的邮箱,更新用户表的邮箱
        String email = examIdcardReq.getEmail();
        if (StrUtil.isNotBlank(email)) {
            userService.update(new LambdaUpdateWrapper<UserDO>().eq(UserDO::getId, userDO.getId())
                .set(UserDO::getEmail, email));
        }
        examIdcardReq.setEducationVerifyStatus(educationConfig.getNoVerifyList().contains(examIdcardReq.getEducation())
            ? EducationVerifyStatusEnum.PASSED.getValue()
            : EducationVerifyStatusEnum.WAIT.getValue());
        return super.add(examIdcardReq);
    }

    /**
     * 获取当前人员的实名信息
     * 
     * @return
     */
    @Override
    public ExamIdcardResp getRealNameInfo() {
        UserDetailResp userDetailResp = userService.get(TokenLocalThreadUtil.get().getUserId());
        ValidationUtils.throwIfNull(userDetailResp, "未登录");
        ExamIdcardResp examIdcardResp = new ExamIdcardResp();
        ExamIdcardDO examIdcardDO = baseMapper.selectOne(new LambdaQueryWrapper<ExamIdcardDO>()
            .eq(ExamIdcardDO::getIdCardNumber, userDetailResp.getUsername()));
        BeanUtil.copyProperties(examIdcardDO, examIdcardResp);
        return examIdcardResp;
    }

    /**
     * 提交学历认证
     * 
     * @param examIdcardReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitVerify(ExamIdcardReq examIdcardReq) {
        Long id = examIdcardReq.getId();
        ExamIdcardDO examIdcardDO = baseMapper.selectById(id);
        ValidationUtils.throwIfNull(examIdcardDO, "学历信息不存在");
        String education = examIdcardReq.getEducation();
        boolean isNoVerify = educationConfig.getNoVerifyList().contains(education);
        String educationCertificate = examIdcardReq.getEducationCertificate();
        ValidationUtils.throwIf(!isNoVerify && ObjectUtil.isNull(educationCertificate), "未上传学信网学历验证报告");
        ExamIdcardDO update = new ExamIdcardDO();
        update.setId(id);
        update.setEducation(education);
        update.setEducationCertificate(educationCertificate);
        update.setEducationVerifyStatus(isNoVerify
            ? EducationVerifyStatusEnum.PASSED.getValue()
            : EducationVerifyStatusEnum.PENDING.getValue());
        update.setEducationVerifyTime(EducationVerifyStatusEnum.REJECTED.getValue()
            .equals(examIdcardDO.getEducationVerifyStatus()) ? null : LocalDateTime.now());
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 审核
     * 
     * @param
     * @return
     */
    @Override
    public Boolean auditExamIdCard(AuditExamIdCardReq req) {
        List<ExamIdcardDO> examIdCardDOS = baseMapper.selectByIds(req.getIds());
        ValidationUtils.throwIfEmpty(examIdCardDOS, "所选审核数据不存在");
        Integer status = req.getStatus();
        ValidationUtils.throwIf(EducationVerifyStatusEnum.REJECTED.getValue().equals(status) && ObjectUtil.isNull(req
            .getRemark()), "未填写驳回理由");
        List<ExamIdcardDO> updateList = examIdCardDOS.stream().map(item -> {
            ExamIdcardDO examIdcardDO = new ExamIdcardDO();
            examIdcardDO.setId(item.getId());
            examIdcardDO.setEducationVerifyStatus(status);
            examIdcardDO.setEducationVerifyRemark(req.getRemark());
            return examIdcardDO;
        }).toList();
        return baseMapper.updateBatchById(updateList);
    }
}