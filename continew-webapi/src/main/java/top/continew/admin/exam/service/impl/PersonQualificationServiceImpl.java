package top.continew.admin.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import top.continew.admin.common.model.entity.IdCardDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.entity.ExamLocationDO;
import top.continew.admin.exam.model.entity.LicenseCertificateDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.PersonQualificationMapper;
import top.continew.admin.exam.model.entity.PersonQualificationDO;
import top.continew.admin.exam.model.query.PersonQualificationQuery;
import top.continew.admin.exam.model.req.PersonQualificationReq;
import top.continew.admin.exam.model.resp.PersonQualificationDetailResp;
import top.continew.admin.exam.model.resp.PersonQualificationResp;
import top.continew.admin.exam.service.PersonQualificationService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员复审信息表业务实现
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Service
@RequiredArgsConstructor
public class PersonQualificationServiceImpl extends BaseServiceImpl<PersonQualificationMapper, PersonQualificationDO, PersonQualificationResp, PersonQualificationDetailResp, PersonQualificationQuery, PersonQualificationReq> implements PersonQualificationService {

    @Resource
    private final LicenseCertificateMapper LicenseCertificateMapper;

    @Resource
    private final AESWithHMAC aesWithHMAC;

    public PageResp<PersonQualificationResp> page(PersonQualificationQuery query, PageQuery pageQuery) {

        //构建分页和查询条件
        Page<PersonQualificationDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        QueryWrapper<PersonQualificationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("create_time");
        if (query.getName() != null) {
            queryWrapper.like("name", query.getName());
        }
        if (query.getIdCard() != null) {
            queryWrapper.eq("id_card", query.getIdCard());
        }
        IPage<PersonQualificationDO> pageResult = baseMapper.selectPage(page, queryWrapper);

        List<PersonQualificationResp> respList = pageResult.getRecords().stream()
                .map(doObj -> {
                    PersonQualificationResp resp = BeanUtil.copyProperties(doObj, PersonQualificationResp.class);
                    resp.setIdCard(aesWithHMAC.verifyAndDecrypt(doObj.getIdCard()));
                    return resp;
                })
                .collect(Collectors.toList());
        return new PageResp<>(respList, pageResult.getTotal());
    }

    @Override
    public Long add(PersonQualificationReq req) {
        //解密身份证号码
        String IdCard = SecureUtils.decryptByRsaPrivateKey(req.getIdCard());
        //查询许可证书
        LicenseCertificateDO licenseCertificateDO =
                LicenseCertificateMapper.selectOne(
                        new LambdaQueryWrapper<LicenseCertificateDO>()
                                .eq(LicenseCertificateDO::getIdcardNo,aesWithHMAC.encryptAndSign(IdCard))
                                .eq(LicenseCertificateDO::getPsnName, req.getName())
                                .eq(LicenseCertificateDO::getPsnlcnsItemCode, req.getQualificationCategoryCode())

                );

        if (licenseCertificateDO == null) {
            throw new BusinessException("未查询到可复审信息");
        }

        LocalDate now = LocalDate.now();
        LocalDate endDate = licenseCertificateDO.getEndDate();

        if (endDate == null) {
            throw new BusinessException("证书有效期缺失，不能复审");
        }

        // 当前年月
        YearMonth nowYM = YearMonth.from(now);
        // 证书到期年月
        YearMonth endYM = YearMonth.from(endDate);
        // 复审开始年月（到期前3个月）
        YearMonth reviewStartYM = endYM.minusMonths(3);

        // 已过有效期
        if (nowYM.isAfter(endYM)) {
            throw new BusinessException("证书已过有效期，不能复审");
        }

        // 未到复审时间
        if (nowYM.isBefore(reviewStartYM)) {
            throw new BusinessException("尚未到复审时间，不能复审");
        }
        // 复审记录
        PersonQualificationDO personQualificationDO = new PersonQualificationDO();
        personQualificationDO.setName(req.getName());
        personQualificationDO.setIdCard(aesWithHMAC.encryptAndSign(IdCard));
        personQualificationDO.setEducation(req.getEducation());
        personQualificationDO.setPhone(req.getPhone());
        personQualificationDO.setEmployer(
                StringUtils.hasText(req.getEmployer())
                        ? req.getEmployer()
                        : licenseCertificateDO.getAuthCom()
        );
        personQualificationDO.setQualificationCategoryCode(req.getQualificationCategoryCode());
        personQualificationDO.setAuditStatus(0); // 待审核

        baseMapper.insert(personQualificationDO);

        return personQualificationDO.getId();
    }

}