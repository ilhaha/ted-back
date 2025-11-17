package top.continew.admin.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.training.mapper.OrgCandidateMapper;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgCandidateDO;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.admin.util.SignUtil;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.TrainingCheckinMapper;
import top.continew.admin.training.model.entity.TrainingCheckinDO;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;
import top.continew.admin.training.service.TrainingCheckinService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 培训签到记录业务实现
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Service
@RequiredArgsConstructor
public class TrainingCheckinServiceImpl extends BaseServiceImpl<TrainingCheckinMapper, TrainingCheckinDO, TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> implements TrainingCheckinService {


    @Autowired
    private TrainingCheckinMapper trainingCheckinMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrgCandidateMapper orgCandidateMapper;
    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private OrgUserMapper orgUserMapper;
    @Value("${qrcode.training.checkin.url}")
    private String trainingCheckinUrl;


    @Override
    public String generateQRCode(Long trainingId) {

        Long userId = TokenLocalThreadUtil.get().getUserId();

        TedOrgUser org = orgUserMapper.selectOne(
                new LambdaQueryWrapper<TedOrgUser>()
                        .eq(TedOrgUser::getUserId, userId)
                        .select(TedOrgUser::getOrgId)
                        .last("LIMIT 1")
        );

        long ts = System.currentTimeMillis();
        String sign = SignUtil.sign(ts);

        String url = String.format(
                "%s?trainingId=%s&orgId=%s&ts=%s&sign=%s",
                trainingCheckinUrl,
                trainingId,
                org.getOrgId(),
                ts,
                sign
        );

        return url;
    }


    @Override
    public boolean doCheckin(String realName, String idCard,
                             Long trainingId, Long orgId,
                             Long ts, String sign) {

        // 先校验二维码是否被伪造
        if (!SignUtil.verify(ts, sign)) {
            throw new BusinessException("二维码参数无效（疑似伪造）");
        }

        //  校验是否过期（5分钟）
        if (System.currentTimeMillis() - ts > 5 * 60 * 1000) {
            throw new BusinessException("二维码已过期，请刷新二维码");
        }

        //加密身份证号
        String encryptedIdcard = aesWithHMAC.encryptAndSign(idCard);
        UserDO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getNickname, realName)
                        .eq(UserDO::getUsername, encryptedIdcard)
                        .last("LIMIT 1")
        );

        if (user == null) {
            throw new BusinessException("姓名或身份证不匹配");
        }

        Long userId = user.getId();
        //判断是否属于该培训
        OrgCandidateDO cand = orgCandidateMapper.selectOne(
                new LambdaQueryWrapper<OrgCandidateDO>()
                        .eq(OrgCandidateDO::getCandidateId, userId)
                        .eq(OrgCandidateDO::getOrgId, orgId)
                        .eq(OrgCandidateDO::getStatus, 2)
                        .last("LIMIT 1")
        );

        if (cand == null) {
            throw new BusinessException("你未报名此培训，无法签到");
        }

        // 今天开始时间和结束时间
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // 查询当天是否已签到
        TrainingCheckinDO existed = trainingCheckinMapper.selectOne(
                new LambdaQueryWrapper<TrainingCheckinDO>()
                        .eq(TrainingCheckinDO::getTrainingId, trainingId)
                        .eq(TrainingCheckinDO::getCandidateId, userId)
                        .ge(TrainingCheckinDO::getCheckinTime, startOfDay)
                        .le(TrainingCheckinDO::getCheckinTime, endOfDay)
                        .last("LIMIT 1")
        );

        if (existed != null) {
            throw new RuntimeException("你今天已签到，无需重复签到");
        }

        // 插入签到记录
        TrainingCheckinDO checkin = new TrainingCheckinDO();
        checkin.setTrainingId(trainingId);
        checkin.setCandidateId(userId);
        checkin.setOrgId(orgId);
        checkin.setQrTimestamp(ts);
        checkin.setQrSign(sign);
        checkin.setStatus(1);
        checkin.setCreateUser(userId);
        checkin.setUpdateUser(userId);

        trainingCheckinMapper.insert(checkin);

        return true;
    }
}