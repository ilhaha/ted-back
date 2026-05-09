package top.continew.admin.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.constant.CertificateUploadRuleConstant;
import top.continew.admin.common.constant.enums.EducationVerifyStatusEnum;
import top.continew.admin.common.constant.enums.ProjectLevelEnum;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.model.entity.ExamIdcardDO;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.vo.UploadedDocumentTypeVO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.LicenseHolderInfoMapper;
import top.continew.admin.exam.model.entity.LicenseHolderInfoDO;
import top.continew.admin.exam.model.query.LicenseHolderInfoQuery;
import top.continew.admin.exam.model.req.LicenseHolderInfoReq;
import top.continew.admin.exam.model.resp.LicenseHolderInfoDetailResp;
import top.continew.admin.exam.model.resp.LicenseHolderInfoResp;
import top.continew.admin.exam.service.LicenseHolderInfoService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 持证信息业务实现
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Service
@RequiredArgsConstructor
public class LicenseHolderInfoServiceImpl extends BaseServiceImpl<LicenseHolderInfoMapper, LicenseHolderInfoDO, LicenseHolderInfoResp, LicenseHolderInfoDetailResp, LicenseHolderInfoQuery, LicenseHolderInfoReq> implements LicenseHolderInfoService {

    /**
     * 判断考生是否都上传了持证信息里面对应的证书
     *
     * @param examIdcardDO
     * @param uploadedDocumentTypes
     */
    @Override
    public void checkLicenseCertificateUploaded(ExamIdcardDO examIdcardDO,
                                                List<UploadedDocumentTypeVO> uploadedDocumentTypes,
                                                Boolean isCertificateExam) {

        ValidationUtils.throwIfEmpty(uploadedDocumentTypes, "您未上传相关证书");

        List<LicenseHolderInfoResp> hasLicenseCertificateList = this.getInfoByUser();

        ValidationUtils.throwIfEmpty(hasLicenseCertificateList, "您未设置持证信息");

        // 已上传证书对应的项目编码
        Set<String> uploadProjectCodes = uploadedDocumentTypes.stream()
                .map(UploadedDocumentTypeVO::getProjectCode)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

        // 已设置持证信息的项目编码
        Set<String> holderProjectCodes = hasLicenseCertificateList.stream()
                .map(LicenseHolderInfoResp::getProjectCode)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

        // 上传了证书，但没有配置持证信息
        Set<String> notSetHolderInfoCodes = new HashSet<>(uploadProjectCodes);
        notSetHolderInfoCodes.removeAll(holderProjectCodes);

        ValidationUtils.throwIfNotEmpty(notSetHolderInfoCodes, "您未设置以下项目的持证信息：" + String.join("、", notSetHolderInfoCodes));

        // 设置了持证信息，但没有上传对应证书
        Set<String> notUploadCertificateCodes = new HashSet<>(holderProjectCodes);
        notUploadCertificateCodes.removeAll(uploadProjectCodes);

        ValidationUtils.throwIfNotEmpty(notUploadCertificateCodes, "您未上传以下项目对应的证书：" + String.join("、", notUploadCertificateCodes));

        // 持证信息有效期已过期
        LocalDate now = LocalDate.now();

        Set<String> expiredProjectCodes = hasLicenseCertificateList.stream()
                .filter(item -> StrUtil.isNotBlank(item.getProjectCode()))
                .filter(item -> item.getValidEndDate() != null)
                .filter(item -> item.getValidEndDate().isBefore(now))
                .map(LicenseHolderInfoResp::getProjectCode)
                .collect(Collectors.toSet());

        ValidationUtils.throwIfNotEmpty(
                expiredProjectCodes,
                "以下项目的持证信息已过期：" + String.join("、", expiredProjectCodes)
        );

        // 如果是取证考生，校验是否满足上传条件
        if (isCertificateExam) {
            canUploadCertificate(
                    examIdcardDO,
                    hasLicenseCertificateList
            );
        }
    }

    /**
     * 判断是否允许上传证书
     * <p>
     * 规则：
     * 1. 理工类本科及以上、理工类大专：直接允许
     * 2. 非理工类本科及以上：需持 I级 持证信息满 6个月
     * 3. 非理工类大专、工学类中专/职高/技校：需持 I级 持证信息满 1年
     * 4. 其他中专/职高/技校/初中/高中：需持 I级 持证信息满 3年
     */
    private void canUploadCertificate(ExamIdcardDO examIdcardDO,
                                         List<LicenseHolderInfoResp> licenseList) {

        String education = examIdcardDO.getEducation();
        String majorType = examIdcardDO.getMajorType();

        // =========================
        // 1. 理工类本科及以上、理工类大专
        // =========================
        if (Objects.equals(
                majorType,
                CertificateUploadRuleConstant.MAJOR_TYPE_SCIENCE)) {

            ValidationUtils.throwIf(!EducationVerifyStatusEnum.PASSED.getValue().equals(examIdcardDO.getEducationVerifyStatus()), "学历未认证" );

            // 理工类本科及以上
            if (isBachelorOrAbove(education)) {
                return;
            }

            // 理工类大专
            if (Objects.equals(
                    education,
                    CertificateUploadRuleConstant.EDUCATION_COLLEGE)) {

                return;
            }
        }

        // 非直接上传情况
        // 所有上传项目都必须满足持证年限要求
        for (LicenseHolderInfoResp license : licenseList) {
            // 只校验 I级持证信息 (如果是二级也包括了一级)
            Integer licenseLevel = license.getProjectLevel();
            String projectCode = license.getProjectCode();
            ValidationUtils.throwIf(!ProjectLevelEnum.LEVEL_ONE.getValue().equals(licenseLevel)
                    && !ProjectLevelEnum.LEVEL_TWO.getValue().equals(licenseLevel),"未填写【" + projectCode + "】项目的持证信息");

            // 持证开始时间
            LocalDate issueDate = license.getValidStartDate();

            // 持证月份
            long months = ChronoUnit.MONTHS.between(issueDate, LocalDate.now());

            // =========================
            // 2. 非理工类本科及以上
            // =========================
            if (isBachelorOrAbove(education)) {

                long monthsSix = CertificateUploadRuleConstant.MONTHS_SIX;

                ValidationUtils.throwIf(months < monthsSix,
                        "持有【" + projectCode + "】项目证书不足" + monthsSix + "个月" );
                return;
            }

            // =========================
            // 3. 非理工类大专、工学类中专/职高/技校
            // =========================
            if (Objects.equals(
                    education,
                    CertificateUploadRuleConstant.EDUCATION_COLLEGE)
                    || isTechnicalSecondaryEducation(education, majorType)) {

                long monthsTwelve = CertificateUploadRuleConstant.MONTHS_TWELVE;
                ValidationUtils.throwIf(months < monthsTwelve,
                        "持有【" + projectCode + "】项目证书不足" + monthsTwelve + "个月" );
                return;
            }

            // =========================
            // 4. 其他中专/职高/技校/初中/高中
            // =========================
            long monthsThirtySix = CertificateUploadRuleConstant.MONTHS_THIRTY_SIX;
            ValidationUtils.throwIf(months < monthsThirtySix,
                    "持有【" + projectCode + "】项目证书不足" + monthsThirtySix + "个月" );
        }
    }

    /**
     * 是否本科及以上
     */
    private boolean isBachelorOrAbove(String education) {

        return Objects.equals(
                education,
                CertificateUploadRuleConstant.EDUCATION_BACHELOR)
                || Objects.equals(
                education,
                CertificateUploadRuleConstant.EDUCATION_BACHELOR_OR_ABOVE);
    }

    /**
     * 是否工学类中专/职高/技校
     */
    private boolean isTechnicalSecondaryEducation(String education,
                                                  String majorType) {
        boolean secondaryEducation =
                Objects.equals(
                        education,
                        CertificateUploadRuleConstant.EDUCATION_SECONDARY)
                        || Objects.equals(
                        education,
                        CertificateUploadRuleConstant.EDUCATION_VOCATIONAL_HIGH)
                        || Objects.equals(
                        education,
                        CertificateUploadRuleConstant.EDUCATION_TECHNICAL_SCHOOL);

        return secondaryEducation
                && Objects.equals(
                majorType,
                CertificateUploadRuleConstant.MAJOR_TYPE_ENGINEERING);
    }

    /**
     * 获取当前用户的持证信息
     *
     * @return
     */
    @Override
    public List<LicenseHolderInfoResp> getInfoByUser() {
        Long userId = TokenLocalThreadUtil.get().getUserId();
        List<LicenseHolderInfoDO> licenseHolderInfoDOS = baseMapper.selectList(new LambdaQueryWrapper<LicenseHolderInfoDO>()
                .eq(LicenseHolderInfoDO::getExamineeId, userId));
        if (ObjectUtil.isEmpty(licenseHolderInfoDOS)) {
            return Collections.emptyList();
        }
        return licenseHolderInfoDOS.stream().map(item -> {
            LicenseHolderInfoResp licenseHolderInfoResp = new LicenseHolderInfoResp();
            BeanUtil.copyProperties(item, licenseHolderInfoResp);
            return licenseHolderInfoResp;
        }).toList();
    }

    /**
     * 保存用户持证信息
     *
     * @param reqs
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveLicenseHolderInfo(List<LicenseHolderInfoReq> reqs) {
        Long userId = TokenLocalThreadUtil.get().getUserId();
        ValidationUtils.throwIfNull(userId, "未登录");
        // 先把之前的持证信息都删除了
        baseMapper.delete(new LambdaQueryWrapper<LicenseHolderInfoDO>()
                .eq(LicenseHolderInfoDO::getExamineeId, userId));
        if (ObjectUtil.isEmpty(reqs)) {
            return Boolean.TRUE;
        }
        // 添加新的持证信息
        List<LicenseHolderInfoDO> insertList = reqs.stream().map(item -> {
            LicenseHolderInfoDO licenseHolderInfoDO = new LicenseHolderInfoDO();
            licenseHolderInfoDO.setExamineeId(userId);
            licenseHolderInfoDO.setProjectCode(item.getProjectCode());
            licenseHolderInfoDO.setValidStartDate(item.getValidStartDate());
            licenseHolderInfoDO.setValidEndDate(item.getValidEndDate());
            licenseHolderInfoDO.setProjectLevel(item.getProjectLevel());
            return licenseHolderInfoDO;
        }).toList();
        baseMapper.insertBatch(insertList);
        return Boolean.TRUE;
    }
}