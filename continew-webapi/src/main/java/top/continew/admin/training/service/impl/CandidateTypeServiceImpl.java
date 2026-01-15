package top.continew.admin.training.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.continew.admin.common.constant.BlacklistConstants;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
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


    /**
     * 重写page  查询作业人员信息
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<CandidateTypeResp> page(CandidateTypeQuery query, PageQuery pageQuery) {
        QueryWrapper<CandidateTypeDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tct.is_deleted", 0);
        String candidateName = query.getCandidateName();
        if (StringUtil.isNotBlank(candidateName)) {
            queryWrapper.like("su.nickname", candidateName);
        }
        String idNumber = query.getIdNumber();
        if (StringUtil.isNotBlank(idNumber)) {
            queryWrapper.eq("su.username", aesWithHMAC.encryptAndSign(idNumber));
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
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean blacklistSwitch(CandidateTypeReq req) {
        CandidateTypeDO candidateTypeDO = baseMapper.selectById(req.getId());
        ValidationUtils.throwIfNull(candidateTypeDO, "所操作的用户信息不存在");

        LambdaUpdateWrapper<CandidateTypeDO> updateWrapper =
                new LambdaUpdateWrapper<CandidateTypeDO>()
                        .eq(CandidateTypeDO::getId, req.getId());

        LocalDateTime now = LocalDateTime.now();

        // ===== 加入黑名单 =====
        if (BlacklistConstants.IS_BLACKLIST.equals(req.getIsBlacklist())) {

            Integer durationType = req.getBlacklistDurationType();
            ValidationUtils.throwIfNull(durationType, "请选择黑名单时长类型");
            ValidationUtils.throwIfEmpty(req.getBlacklistReason(), "请输入加入黑名单原因");

            LocalDateTime endTime = calculateBlacklistEndTime(now, durationType);

            updateWrapper
                    .set(CandidateTypeDO::getIsBlacklist, BlacklistConstants.IS_BLACKLIST)
                    .set(CandidateTypeDO::getBlacklistDurationType, durationType)
                    .set(CandidateTypeDO::getBlacklistReason, req.getBlacklistReason())
                    .set(CandidateTypeDO::getBlacklistTime, now)
                    .set(CandidateTypeDO::getBlacklistEndTime, endTime);

        } else {
            // ===== 解除黑名单 =====
            updateWrapper
                    .set(CandidateTypeDO::getIsBlacklist, BlacklistConstants.NOT_BLACKLIST)
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


}