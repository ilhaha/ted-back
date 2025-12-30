package top.continew.admin.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.model.entity.IdCardDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.exam.mapper.LicenseCertificateMapper;
import top.continew.admin.exam.model.dto.ExamPlanExcelRowDTO;
import top.continew.admin.exam.model.entity.*;
import top.continew.starter.core.exception.BusinessException;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    @Override
    public PersonQualificationDetailResp get(Long id) {
        // 查询复审记录
        PersonQualificationDO personQualificationDO = baseMapper.selectById(id);
        if (personQualificationDO == null) {
            throw new BusinessException("复审记录不存在");
        }
        PersonQualificationDetailResp resp = BeanUtil.copyProperties(personQualificationDO, PersonQualificationDetailResp.class);
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
            String[] expectedHeaders = {
                    "姓名",
                    "身份证号",
                    "文化程度",
                    "联系电话",
                    "聘用单位",
                    "资格项目代码"
            };
            for (int i = 0; i < expectedHeaders.length; i++) {
                String actual = getCellString(headerRow.getCell(i));
                if (!expectedHeaders[i].equals(actual)) {
                    throw new BusinessException(
                            "模板错误：第" + (i + 1) + "列应为【" + expectedHeaders[i] + "】，实际为【" + actual + "】"
                    );
                }
            }

            // 读取数据行
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int rowIndex = i + 1;
                try {
                    String name = getCellString(row.getCell(0));
                    String idCard = getCellString(row.getCell(1));
                    String education = getCellString(row.getCell(2));
                    String phone = getCellString(row.getCell(3));
                    String employer = getCellString(row.getCell(4));
                    String qualificationCode = getCellString(row.getCell(5));

                    // 空行跳过
                    if (!StringUtils.hasText(name) && !StringUtils.hasText(idCard)) continue;

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
                        throw new BusinessException(
                                "姓名+身份证号+资格项目代码在 Excel 内重复（首次出现在第"
                                        + uniqueKeyRowMap.get(uniqueKey) + "行）"
                        );
                    }
                    uniqueKeyRowMap.put(uniqueKey, rowIndex);

                    // 身份证加密
                    String encryptedIdCard = aesWithHMAC.encryptAndSign(idCard);

                    // 查询许可证书
                    LicenseCertificateDO licenseCertificateDO = LicenseCertificateMapper.selectOne(
                            new LambdaQueryWrapper<LicenseCertificateDO>()
                                    .eq(LicenseCertificateDO::getIdcardNo, encryptedIdCard)
                                    .eq(LicenseCertificateDO::getPsnName, name)
                                    .eq(LicenseCertificateDO::getPsnlcnsItemCode, qualificationCode)
                    );
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
                    boolean exists = baseMapper.exists(
                            new LambdaQueryWrapper<PersonQualificationDO>()
                                    .eq(PersonQualificationDO::getIdCard, encryptedIdCard)
                                    .eq(PersonQualificationDO::getName, name)
                                    .eq(PersonQualificationDO::getQualificationCategoryCode, qualificationCode)
                                    .eq(PersonQualificationDO::getIsDeleted, 0)
                    );
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

    /**
     * 安全读取单元格内容（统一转String）
     */
    private String getCellString(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}