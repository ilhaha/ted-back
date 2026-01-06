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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.LicenseCertificateConstant;
import top.continew.admin.common.constant.PersonQualificationConstant;
import top.continew.admin.common.constant.enums.ReviewStatusEnum;
import top.continew.admin.common.model.resp.ImportResultVO;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.entity.*;
import top.continew.admin.exam.model.req.PersonQualificationAuditReq;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.PersonQualificationMapper;
import top.continew.admin.exam.model.query.PersonQualificationQuery;
import top.continew.admin.exam.model.req.PersonQualificationReq;
import top.continew.admin.exam.model.resp.PersonQualificationDetailResp;
import top.continew.admin.exam.model.resp.PersonQualificationResp;
import top.continew.admin.exam.service.PersonQualificationService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
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
        String idCard = query.getIdCard();
        if (StrUtil.isNotBlank(idCard)) {
            queryWrapper.eq("id_card", aesWithHMAC.encryptAndSign(idCard));
        }
        IPage<PersonQualificationDO> pageResult = baseMapper.selectPage(page, queryWrapper);

        List<PersonQualificationResp> respList = pageResult.getRecords().stream().map(doObj -> {
            PersonQualificationResp resp = BeanUtil.copyProperties(doObj, PersonQualificationResp.class);
            resp.setIdCard(aesWithHMAC.verifyAndDecrypt(doObj.getIdCard()));
            return resp;
        }).collect(Collectors.toList());
        return new PageResp<>(respList, pageResult.getTotal());
    }

    @Override
    public Long add(PersonQualificationReq req) {
        //解密身份证号码
        String IdCard = SecureUtils.decryptByRsaPrivateKey(req.getIdCard());
        //查询许可证书
        LicenseCertificateDO licenseCertificateDO = LicenseCertificateMapper
            .selectOne(new LambdaQueryWrapper<LicenseCertificateDO>().eq(LicenseCertificateDO::getIdcardNo, aesWithHMAC
                .encryptAndSign(IdCard))
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
        YearMonth reviewStartYM = endYM.minusMonths(PersonQualificationConstant.REVIEW_ADVANCE_MONTHS);

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
        personQualificationDO.setEmployer(StringUtils.hasText(req.getEmployer())
            ? req.getEmployer()
            : licenseCertificateDO.getAuthCom());
        personQualificationDO.setQualificationCategoryCode(req.getQualificationCategoryCode());
        personQualificationDO.setAuditStatus(0); // 待审核
        baseMapper.insert(personQualificationDO);

        return personQualificationDO.getId();
    }

    @Override
    public PersonQualificationDetailResp get(Long id) {
        // 查询复审记录
        PersonQualificationDO personQualificationDO = baseMapper.selectById(id);
        if (personQualificationDO == null) {
            throw new BusinessException("复审记录不存在");
        }
        PersonQualificationDetailResp resp = BeanUtil
            .copyProperties(personQualificationDO, PersonQualificationDetailResp.class);
        resp.setIdCard(aesWithHMAC.verifyAndDecrypt(personQualificationDO.getIdCard()));
        return resp;
    }

    @Override
    public void update(PersonQualificationReq req, Long id) {
        // 查询复审记录
        PersonQualificationDO personQualificationDO = baseMapper.selectById(id);
        if (personQualificationDO == null) {
            throw new BusinessException("复审记录不存在");
        }
        // 只能更新待审核状态的记录
        if (personQualificationDO.getAuditStatus() != 0) {
            throw new BusinessException("只能更新待审核状态的复审记录");
        }
        personQualificationDO.setEducation(req.getEducation());
        personQualificationDO.setPhone(req.getPhone());
        personQualificationDO.setEmployer(req.getEmployer());
        baseMapper.updateById(personQualificationDO);
    }

    /**
     * 批量导入复审人员信息
     *
     * @param file Excel 文件
     */
    @Transactional
    @Override
    public void importExcel(MultipartFile file) {

        // 文件校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("仅支持 Excel 文件（.xlsx / .xls）");
        }

        // 错误收集
        List<String> errorMessages = new ArrayList<>();

        // 合法数据列表
        List<PersonQualificationDO> insertList = new ArrayList<>();
        // Excel 内组合唯一性校验 key = 姓名|身份证号|资格项目代码
        Map<String, Integer> uniqueKeyRowMap = new HashMap<>();

        try (InputStream is = file.getInputStream()) {

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel 中未找到有效工作表");
            }

            // 第 1 行表头
            int headerRowIndex = 0;
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new BusinessException("Excel 表头不存在或模板错误");
            }

            // 表头校验（表头错误直接终止）
            String[] expectedHeaders = {"姓名", "身份证号", "文化程度", "联系电话", "聘用单位", "资格项目代码"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                String actual = getCellString(headerRow.getCell(i));
                if (!expectedHeaders[i].equals(actual)) {
                    throw new BusinessException("模板错误：第" + (i + 1) + "列应为【" + expectedHeaders[i] + "】，实际为【" + actual + "】");
                }
            }

            // 读取数据行
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                int rowIndex = i + 1;
                try {
                    String name = getCellString(row.getCell(0));
                    String idCard = getCellString(row.getCell(1));
                    String education = getCellString(row.getCell(2));
                    String phone = getCellString(row.getCell(3));
                    String employer = getCellString(row.getCell(4));
                    String qualificationCode = getCellString(row.getCell(5));

                    // 空行跳过
                    if (!StringUtils.hasText(name) && !StringUtils.hasText(idCard))
                        continue;

                    // 非空校验
                    if (!StringUtils.hasText(name)) {
                        throw new BusinessException("姓名不能为空");
                    }
                    if (!StringUtils.hasText(idCard)) {
                        throw new BusinessException("身份证号不能为空");
                    }
                    if (!StringUtils.hasText(qualificationCode)) {
                        throw new BusinessException("资格项目代码不能为空");
                    }

                    // Excel 内组合唯一性校验
                    String uniqueKey = name + "|" + idCard + "|" + qualificationCode;
                    if (uniqueKeyRowMap.containsKey(uniqueKey)) {
                        throw new BusinessException("姓名+身份证号+资格项目代码在 Excel 内重复（首次出现在第" + uniqueKeyRowMap
                            .get(uniqueKey) + "行）");
                    }
                    uniqueKeyRowMap.put(uniqueKey, rowIndex);

                    // 身份证加密
                    String encryptedIdCard = aesWithHMAC.encryptAndSign(idCard);

                    // 查询许可证书
                    LicenseCertificateDO licenseCertificateDO = LicenseCertificateMapper
                        .selectOne(new LambdaQueryWrapper<LicenseCertificateDO>()
                            .eq(LicenseCertificateDO::getIdcardNo, encryptedIdCard)
                            .eq(LicenseCertificateDO::getPsnName, name)
                            .eq(LicenseCertificateDO::getPsnlcnsItemCode, qualificationCode));
                    if (licenseCertificateDO == null) {
                        throw new BusinessException("未查询到可复审的证书信息");
                    }

                    // 校验证书复审时间窗口
                    LocalDate endDate = licenseCertificateDO.getEndDate();
                    if (endDate == null) {
                        throw new BusinessException("证书有效期缺失，不能复审");
                    }

                    YearMonth nowYM = YearMonth.now();
                    YearMonth endYM = YearMonth.from(endDate);
                    YearMonth reviewStartYM = endYM.minusMonths(3);

                    if (nowYM.isAfter(endYM)) {
                        throw new BusinessException("证书已过有效期，不能复审");
                    }
                    if (nowYM.isBefore(reviewStartYM)) {
                        throw new BusinessException("尚未到复审时间，不能复审");
                    }

                    // 数据库组合唯一性校验
                    boolean exists = baseMapper.exists(new LambdaQueryWrapper<PersonQualificationDO>()
                        .eq(PersonQualificationDO::getIdCard, encryptedIdCard)
                        .eq(PersonQualificationDO::getName, name)
                        .eq(PersonQualificationDO::getQualificationCategoryCode, qualificationCode)
                        .eq(PersonQualificationDO::getIsDeleted, 0));
                    if (exists) {
                        throw new BusinessException("姓名+身份证号+资格项目代码在系统中已存在");
                    }

                    // 构建实体
                    PersonQualificationDO entity = new PersonQualificationDO();
                    entity.setName(name);
                    entity.setIdCard(encryptedIdCard);
                    entity.setEducation(education);
                    entity.setPhone(phone);
                    entity.setEmployer(StringUtils.hasText(employer) ? employer : licenseCertificateDO.getAuthCom());
                    entity.setQualificationCategoryCode(qualificationCode);
                    entity.setAuditStatus(0);

                    insertList.add(entity);

                } catch (BusinessException e) {
                    errorMessages.add("第" + rowIndex + "行：" + e.getMessage());
                } catch (Exception e) {
                    errorMessages.add("第" + rowIndex + "行：数据解析异常");
                }
            }

            // 如果有错误 → 统一抛出，不入库
            if (!errorMessages.isEmpty()) {
                throw new BusinessException(String.join("\n", errorMessages));
            }

            // 批量入库
            if (!insertList.isEmpty()) {
                baseMapper.insertBatch(insertList);
            }

        } catch (EncryptedDocumentException e) {
            throw new BusinessException("Excel 文件被加密，无法读取");
        } catch (IOException e) {
            throw new BusinessException("Excel 文件读取失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(PersonQualificationAuditReq req) {

        // 查询复审记录
        PersonQualificationDO entity = this.getById(req.getId());
        if (entity == null) {
            throw new BusinessException("复审记录不存在");
        }

        // 只能审核【待审核】
        if (!Objects.equals(entity.getAuditStatus(), 0)) {
            throw new BusinessException("该记录已审核，不能重复操作");
        }

        Integer auditStatus = req.getAuditStatus();

        PersonQualificationDO update = new PersonQualificationDO();
        update.setId(entity.getId());

        // 审核逻辑
        if (Objects.equals(auditStatus, 1)) {
            //审核通过 → 修改许可证书
            update.setAuditStatus(1);
            // 查询许可证书
            LicenseCertificateDO certificate = LicenseCertificateMapper
                .selectOne(new LambdaQueryWrapper<LicenseCertificateDO>().eq(LicenseCertificateDO::getIdcardNo, entity
                    .getIdCard())
                    .eq(LicenseCertificateDO::getPsnName, entity.getName())
                    .eq(LicenseCertificateDO::getPsnlcnsItemCode, entity.getQualificationCategoryCode()));
            if (certificate == null) {
                throw new BusinessException("未找到对应的许可证书，无法完成复审");
            }
            // ====== 更新许可证书 ======
            // 审核通过时间（当天）
            LocalDate auditDate = LocalDate.now();
            // 新有效期 = 审核时间 + 4 年
            LocalDate newEndDate = auditDate.plusYears(4);
            certificate.setEndDate(newEndDate);

            // 更新时间（证书签发日期、授权日期、证书有效期）
            certificate.setCertDate(LocalDate.now());
            certificate.setAuthDate(LocalDate.now());
            certificate.setUpdateTime(LocalDateTime.now());

            //更新单位
            certificate.setOriginalComName(certificate.getComName());
            certificate.setComName(entity.getEmployer());

            int rows = LicenseCertificateMapper.updateById(certificate);
            if (rows <= 0) {
                throw new BusinessException("许可证书更新失败");
            }

        }
        //        else if (Objects.equals(auditStatus, 2)) {
        //            // 审核不通过
        //            // ===============================
        //            update.setAuditStatus(2);
        //
        //            // 一般不通过不修改证书
        //            // 如需记录，可在复审表中存 remark
        //
        //        }
        else {
            throw new BusinessException("非法的审核状态");
        }

        //更新复审记录
        update.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(update);
        if (!success) {
            throw new BusinessException("审核失败，请重试");
        }
    }

    /**
     * 批量审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchAudit(List<Long> ids) {
        ValidationUtils.throwIfEmpty(ids, "未选择所需审核的复审记录");

        // 查询复审记录
        List<PersonQualificationDO> auditList = this.listByIds(ids);

        // 校验状态（只能是待审核）
        List<PersonQualificationDO> invalidList = auditList.stream()
            .filter(item -> !Objects.equals(item.getAuditStatus(), ReviewStatusEnum.PENDING_REVIEW.getValue()))
            .toList();

        ValidationUtils.throwIfNotEmpty(invalidList, "所选记录中包含已审核的复审信息，请重新选择");

        // 批量查询许可证书
        List<LicenseCertificateDO> certificates = LicenseCertificateMapper
            .selectList(new LambdaQueryWrapper<LicenseCertificateDO>().in(LicenseCertificateDO::getIdcardNo, auditList
                .stream()
                .map(PersonQualificationDO::getIdCard)
                .toList()));
        ValidationUtils.throwIfEmpty(certificates, "未查询到复审记录对应的许可证书信息");

        // 构建证书映射（身份证+姓名+项目编码 唯一）
        Map<String, LicenseCertificateDO> certificateMap = certificates.stream()
            .collect(Collectors.toMap(c -> buildCertKey(c.getIdcardNo(), c.getPsnName(), c
                .getPsnlcnsItemCode()), Function.identity(), (a, b) -> a));

        LocalDate auditDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<LicenseCertificateDO> needUpdateCertList = new ArrayList<>();

        for (PersonQualificationDO entity : auditList) {

            String key = buildCertKey(entity.getIdCard(), entity.getName(), entity.getQualificationCategoryCode());

            LicenseCertificateDO certificate = certificateMap.get(key);
            ValidationUtils.throwIfNull(certificate, "未找到身份证为【" + entity.getIdCard() + "】，项目编码为【" + entity
                .getQualificationCategoryCode() + "】的许可证书");

            // ===== 更新许可证书对象=====
            certificate.setEndDate(auditDate.plusYears(LicenseCertificateConstant.VALIDITY_PERIOD_YEARS));
            certificate.setCertDate(auditDate);
            certificate.setAuthDate(auditDate);
            certificate.setUpdateTime(now);
            certificate.setOriginalComName(certificate.getComName());
            certificate.setComName(entity.getEmployer());
            needUpdateCertList.add(certificate);

            // ===== 更新复审记录 =====
            entity.setAuditStatus(ReviewStatusEnum.APPROVED.getValue());
            entity.setUpdateTime(now);
        }

        // 批量更新许可证书
        if (!needUpdateCertList.isEmpty()) {
            LicenseCertificateMapper.updateBatchById(needUpdateCertList);
        }
        // 批量更新复审记录
        this.updateBatchById(auditList);

        return Boolean.TRUE;
    }

    /**
     * 解析Excel
     * 
     * @param file
     * @return
     */
    @Override
    public ImportResultVO<PersonQualificationDO> analysisExcel(MultipartFile file) {
        ImportResultVO<PersonQualificationDO> result = new ImportResultVO<>();

        // 文件校验（仍然可以直接抛）
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("仅支持 Excel 文件（.xlsx / .xls）");
        }

        // Excel 内唯一性校验
        Map<String, Integer> uniqueKeyRowMap = new HashMap<>();

        try (InputStream is = file.getInputStream()) {

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel 中未找到有效工作表");
            }

            // 表头校验（仍然直接终止）
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BusinessException("Excel 表头不存在或模板错误");
            }

            String[] expectedHeaders = PersonQualificationConstant.EXCEL_HEADERS;
            for (int i = 0; i < expectedHeaders.length; i++) {
                String actual = getCellString(headerRow.getCell(i));
                if (!expectedHeaders[i].equals(actual)) {
                    throw new BusinessException("模板错误：第" + (i + 1) + "列应为【" + expectedHeaders[i] + "】");
                }
            }

            // 读取数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                int rowNum = i + 1;

                try {
                    // ===== 读取 =====
                    String name = getCellString(row.getCell(0));
                    String idCard = getCellString(row.getCell(1));
                    String education = getCellString(row.getCell(2));
                    String phone = getCellString(row.getCell(3));
                    String employer = getCellString(row.getCell(4));
                    String qualificationCode = getCellString(row.getCell(5));

                    if (!StringUtils.hasText(name) && !StringUtils.hasText(idCard)) {
                        continue; // 空行
                    }

                    // ===== 校验 =====
                    if (!StringUtils.hasText(name)) {
                        throw new BusinessException("姓名不能为空");
                    }
                    if (!StringUtils.hasText(idCard)) {
                        throw new BusinessException("身份证号不能为空");
                    }
                    if (!StringUtils.hasText(qualificationCode)) {
                        throw new BusinessException("资格项目代码不能为空");
                    }

                    String uniqueKey = name + "|" + idCard + "|" + qualificationCode;
                    if (uniqueKeyRowMap.containsKey(uniqueKey)) {
                        throw new BusinessException("Excel 内重复（首次出现在第" + uniqueKeyRowMap.get(uniqueKey) + "行）");
                    }
                    uniqueKeyRowMap.put(uniqueKey, rowNum);

                    String encryptedIdCard = aesWithHMAC.encryptAndSign(idCard);

                    LicenseCertificateDO cert = LicenseCertificateMapper
                        .selectOne(new LambdaQueryWrapper<LicenseCertificateDO>()
                            .eq(LicenseCertificateDO::getIdcardNo, encryptedIdCard)
                            .eq(LicenseCertificateDO::getPsnName, name)
                            .eq(LicenseCertificateDO::getPsnlcnsItemCode, qualificationCode));
                    if (cert == null) {
                        throw new BusinessException("未查询到可复审的证书信息");
                    }

                    LocalDate endDate = cert.getEndDate();
                    if (endDate == null) {
                        throw new BusinessException("证书有效期缺失");
                    }

                    YearMonth nowYM = YearMonth.now();
                    YearMonth endYM = YearMonth.from(endDate);
                    YearMonth reviewStartYM = endYM.minusMonths(PersonQualificationConstant.REVIEW_ADVANCE_MONTHS);

                    if (nowYM.isBefore(reviewStartYM)) {
                        throw new BusinessException(String.format("尚未到复审时间，复审时间从 %s 开始", reviewStartYM));
                    }

                    if (nowYM.isAfter(endYM)) {
                        throw new BusinessException(String.format("证书已过有效期（%s），无法复审", endYM));
                    }

                    boolean exists = baseMapper.exists(new LambdaQueryWrapper<PersonQualificationDO>()
                        .eq(PersonQualificationDO::getIdCard, encryptedIdCard)
                        .eq(PersonQualificationDO::getName, name)
                        .eq(PersonQualificationDO::getQualificationCategoryCode, qualificationCode));
                    if (exists) {
                        throw new BusinessException("系统中已存在该复审记录");
                    }

                    // ===== 构建成功数据 =====
                    PersonQualificationDO entity = new PersonQualificationDO();
                    entity.setName(name);
                    entity.setIdCard(idCard);
                    entity.setEducation(education);
                    entity.setPhone(phone);
                    entity.setEmployer(StringUtils.hasText(employer) ? employer : cert.getAuthCom());
                    entity.setQualificationCategoryCode(qualificationCode);
                    entity.setAuditStatus(0);
                    entity.setIsUpload(Boolean.TRUE);
                    result.getSuccessList().add(entity);

                } catch (BusinessException e) {
                    result.getFailList().add(buildFail(rowNum, row, e.getMessage()));
                } catch (Exception e) {
                    result.getFailList().add(buildFail(rowNum, row, "数据解析异常"));
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        return result;
    }

    /**
     * 批量添加
     * 
     * @param reqs
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchAdd(List<PersonQualificationReq> reqs) {
        ValidationUtils.throwIfEmpty(reqs, "未选择所需导入的数据");

        // 1 先加密所有身份证号
        Map<String, PersonQualificationReq> idCardMap = new HashMap<>();
        reqs.forEach(req -> {
            String encryptedIdCard = aesWithHMAC.encryptAndSign(req.getIdCard());
            // 用 idCard + name + code 作为 key
            String key = buildUniqueKey(encryptedIdCard, req.getName(), req.getQualificationCategoryCode());
            idCardMap.put(key, req);
        });

        Set<String> uniqueKeys = idCardMap.keySet();

        // 2 批量查询数据库中已存在的记录
        if (!uniqueKeys.isEmpty()) {
            // 查询条件：idCard in (...) AND name+code in (...)
            List<PersonQualificationDO> existingList = baseMapper
                .selectList(new LambdaQueryWrapper<PersonQualificationDO>().in(PersonQualificationDO::getIdCard, reqs
                    .stream()
                    .map(r -> aesWithHMAC.encryptAndSign(r.getIdCard()))
                    .toList()));

            // 构建已存在的 key 集合
            Set<String> existsKeySet = existingList.stream()
                .map(e -> buildUniqueKey(e.getIdCard(), e.getName(), e.getQualificationCategoryCode()))
                .collect(Collectors.toSet());

            // 过滤掉已存在的记录
            existsKeySet.forEach(idCardMap::remove);
        }

        // 3 构建待插入实体
        List<PersonQualificationDO> insertList = idCardMap.values().stream().map(req -> {
            PersonQualificationDO entity = new PersonQualificationDO();
            BeanUtil.copyProperties(req, entity);
            entity.setIdCard(aesWithHMAC.encryptAndSign(req.getIdCard()));
            entity.setAuditStatus(ReviewStatusEnum.PENDING_REVIEW.getValue());
            return entity;
        }).toList();

        // 4 批量插入
        if (insertList.isEmpty()) {
            return false;
        }

        return baseMapper.insertBatch(insertList);
    }

    /** 构建组合唯一 key */
    private String buildUniqueKey(String idCard, String name, String code) {
        return idCard + "|" + name + "|" + code;
    }

    private ImportResultVO.ImportFailVO buildFail(int rowNum, Row row, String reason) {
        ImportResultVO.ImportFailVO fail = new ImportResultVO.ImportFailVO();
        fail.setRowNum(rowNum);
        fail.setReason(reason);

        Map<String, Object> rowData = new HashMap<>();
        rowData.put("name", getCellString(row.getCell(0)));
        rowData.put("idCard", getCellString(row.getCell(1)));
        rowData.put("qualificationCode", getCellString(row.getCell(5)));
        fail.setRowData(rowData);

        return fail;
    }

    /**
     * 构建许可证书唯一Key
     */
    private String buildCertKey(String idCard, String name, String itemCode) {
        return idCard + "_" + name + "_" + itemCode;
    }

    /**
     * 安全读取单元格内容（统一转String）
     */
    private String getCellString(Cell cell) {
        if (cell == null)
            return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}