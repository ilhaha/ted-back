package top.continew.admin.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
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

    /**
     * 考生根据身份证号查看是否已实名
     * @param username
     * @return
     */
    @Override
    public Boolean verifyRealName(String username) {
        String decrUsername = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(username));
        System.out.println(decrUsername);
        ValidationUtils.throwIfBlank(username, "用户名解密失败");
        String aesUsername = aesWithHMAC.encryptAndSign(decrUsername);
        System.out.println(aesUsername);
        LambdaQueryWrapper<ExamIdcardDO> examIdcardDOLambdaQueryWrapper =
                new LambdaQueryWrapper<ExamIdcardDO>().eq(ExamIdcardDO::getIdCardNumber, aesUsername);
        return  baseMapper.selectCount(examIdcardDOLambdaQueryWrapper) > 0;
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