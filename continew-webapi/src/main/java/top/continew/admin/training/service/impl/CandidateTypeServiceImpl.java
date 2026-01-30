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

package top.continew.admin.training.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.CryptoException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.constant.BlacklistConstants;
import top.continew.admin.common.constant.enums.CandidateTypeEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.entity.LicenseCertificateDO;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.dto.UserDetailDTO;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.service.UserService;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.admin.training.model.query.CandidateTypeQuery;
import top.continew.admin.training.model.req.CandidateTypeReq;
import top.continew.admin.training.model.resp.CandidateTypeDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeResp;
import top.continew.admin.training.service.CandidateTypeService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考生类型业务实现
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Service
@RequiredArgsConstructor
public class CandidateTypeServiceImpl extends BaseServiceImpl<CandidateTypeMapper, CandidateTypeDO, CandidateTypeResp, CandidateTypeDetailResp, CandidateTypeQuery, CandidateTypeReq> implements CandidateTypeService {

    private final AESWithHMAC aesWithHMAC;

    private final UserService userService;

    private final UserMapper userMapper;

    private final WorkerApplyMapper workerApplyMapper;

    private final LicenseCertificateMapper licenseCertificateMapper;

    /**
     * 重写page 查询作业人员信息
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<CandidateTypeResp> page(CandidateTypeQuery query, PageQuery pageQuery) {
        QueryWrapper<CandidateTypeDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tct.type", CandidateTypeEnum.WORKER.getValue()).eq("tct.is_deleted", 0);
        String candidateName = query.getCandidateName();
        if (StringUtil.isNotBlank(candidateName)) {
            queryWrapper.like("su.nickname", candidateName);
        }
        String idNumber = query.getIdNumber();
        if (StringUtil.isNotBlank(idNumber)) {
            queryWrapper.eq("su.username", aesWithHMAC.encryptAndSign(idNumber));
        }

        String phone = query.getPhone();
        if (StringUtil.isNotBlank(phone)) {
            queryWrapper.eq("su.phone", aesWithHMAC.encryptAndSign(phone));
        }
        IPage<CandidateTypeDetailResp> page = baseMapper.getWorkerPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);
        List<CandidateTypeDetailResp> records = page.getRecords();
        if (ObjectUtil.isNotEmpty(records)) {
            page.setRecords(records.stream().map(item -> {
                item.setUsername(aesWithHMAC.verifyAndDecrypt(item.getUsername()));
                item.setPhone(aesWithHMAC.verifyAndDecrypt(item.getPhone()));
                return item;
            }).toList());
        }
        PageResp<CandidateTypeResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 切换黑名单状态
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean blacklistSwitch(CandidateTypeReq req) {
        CandidateTypeDO candidateTypeDO = baseMapper.selectById(req.getId());
        ValidationUtils.throwIfNull(candidateTypeDO, "所操作的用户信息不存在");

        LambdaUpdateWrapper<CandidateTypeDO> updateWrapper = new LambdaUpdateWrapper<CandidateTypeDO>()
            .eq(CandidateTypeDO::getId, req.getId());

        LocalDateTime now = LocalDateTime.now();

        // ===== 加入黑名单 =====
        if (BlacklistConstants.IS_BLACKLIST.equals(req.getIsBlacklist())) {

            Integer durationType = req.getBlacklistDurationType();
            ValidationUtils.throwIfNull(durationType, "请选择黑名单时长类型");
            ValidationUtils.throwIfEmpty(req.getBlacklistReason(), "请输入加入黑名单原因");

            LocalDateTime endTime = calculateBlacklistEndTime(now, durationType);

            updateWrapper.set(CandidateTypeDO::getIsBlacklist, BlacklistConstants.IS_BLACKLIST)
                .set(CandidateTypeDO::getBlacklistDurationType, durationType)
                .set(CandidateTypeDO::getBlacklistReason, req.getBlacklistReason())
                .set(CandidateTypeDO::getBlacklistTime, now)
                .set(CandidateTypeDO::getBlacklistEndTime, endTime);

        } else {
            // ===== 解除黑名单 =====
            updateWrapper.set(CandidateTypeDO::getIsBlacklist, BlacklistConstants.NOT_BLACKLIST)
                .set(CandidateTypeDO::getBlacklistDurationType, BlacklistConstants.DURATION_NONE)
                .set(CandidateTypeDO::getBlacklistReason, null)
                .set(CandidateTypeDO::getBlacklistTime, null)
                .set(CandidateTypeDO::getBlacklistEndTime, null);
        }

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 计算黑名单结束时间
     */
    private LocalDateTime calculateBlacklistEndTime(LocalDateTime now, Integer durationType) {

        switch (durationType) {
            case BlacklistConstants.DURATION_1_DAY:
                return now.plusDays(1);
            case BlacklistConstants.DURATION_1_MONTH:
                return now.plusMonths(1);
            case BlacklistConstants.DURATION_3_MONTH:
                return now.plusMonths(3);
            case BlacklistConstants.DURATION_6_MONTH:
                return now.plusMonths(6);
            case BlacklistConstants.DURATION_1_YEAR:
                return now.plusYears(1);
            case BlacklistConstants.DURATION_FOREVER:
                return null; // 无期限
            default:
                return null;
        }
    }

    @Override
    public CandidateTypeDetailResp get(Long id) {
        // 查询详情
        CandidateTypeDetailResp originalDetailResp = super.get(id);
        if (ObjectUtils.isEmpty(originalDetailResp)) {
            throw new BusinessException("该作业人员");
        }
        // 创建一个新的 CandidateTypeDetailResp 对象
        CandidateTypeDetailResp newDetailResp = new CandidateTypeDetailResp();
        // 查询用户、解密、赋值操作
        UserDetailDTO userDetailDTO = userService.getUserDetail(originalDetailResp.getCandidateId());
        if (!ObjectUtils.isEmpty(userDetailDTO)) {
            try {
                newDetailResp.setUsername(userDetailDTO.getUsername());
                newDetailResp.setPhone(userDetailDTO.getPhone());
                newDetailResp.setNickname(userDetailDTO.getNickname());
                newDetailResp.setAvatar(userDetailDTO.getAvatar());
                newDetailResp.setCandidateId(originalDetailResp.getCandidateId());
            } catch (Exception e) {
                throw new BusinessException("查询失败", e);
            }
        }
        return newDetailResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CandidateTypeReq req, Long id) {
        //查询用户
        UserDO userDO = userService.getById(req.getCandidateId());
        if (userDO == null) {
            throw new BusinessException("该作业人员不存在");
        }
        String oldUserName = userDO.getUsername();

        try {
            //身份证解密 → 加密
            String rawIdCard = SecureUtils.decryptByRsaPrivateKey(req.getUsername());
            if (rawIdCard == null || rawIdCard.isBlank()) {
                throw new BusinessException("身份证信息解密失败");
            }
            String encryptedIdCard = aesWithHMAC.encryptAndSign(rawIdCard);

            //校验身份证是否重复（排除自己）
            QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", encryptedIdCard);
            queryWrapper.ne("id", userDO.getId());
            boolean exists = userMapper.selectCount(queryWrapper) > 0;
            if (exists) {
                throw new BusinessException("该身份证号已存在，且不属于当前人员");
            }

            userDO.setUsername(encryptedIdCard);

            //电话号码解密 → 加密
            String rawPhone = SecureUtils.decryptByRsaPrivateKey(req.getPhone());
            if (rawPhone == null || rawPhone.isBlank()) {
                throw new BusinessException("电话号码解密失败");
            }
            String encryptedPhone = aesWithHMAC.encryptAndSign(rawPhone);
            // 判断手机号是否存在
            if (userMapper.selectCount(new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, encryptedPhone)
                .ne(UserDO::getId, userDO.getId())) > 0) {
                throw new BusinessException("该手机号码已存在，且不属于当前人员");
            }
            userDO.setPhone(encryptedPhone);

        } catch (CryptoException e) {
            throw new BusinessException("敏感信息加解密失败", e);
        }

        if (req.getNickname() != null) {
            userDO.setNickname(req.getNickname());
        }
        if (req.getAvatar() != null) {
            userDO.setAvatar(req.getAvatar());
        }

        // 更新数据库
        try {
            // 修改该身份证下得所有信息
            String newPhone = userDO.getPhone();
            String newUserName = userDO.getUsername();
            workerApplyMapper.update(new LambdaUpdateWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getIdCardNumber, oldUserName)
                .set(WorkerApplyDO::getIdCardNumber, newUserName)
                .set(WorkerApplyDO::getPhone, newPhone));

            licenseCertificateMapper.update(new LambdaUpdateWrapper<LicenseCertificateDO>()
                .eq(LicenseCertificateDO::getIdcardNo, oldUserName)
                .set(LicenseCertificateDO::getIdcardNo, newUserName));
            userService.updateById(userDO);

        } catch (DuplicateKeyException e) {
            // 数据库唯一索引冲突（身份证重复）
            throw new BusinessException("该身份证已存在，请重新输入", e);
        } catch (Exception e) {
            throw new BusinessException("更新用户信息失败", e);
        }
    }

}