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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import net.dreamlu.mica.core.utils.BeanUtil;
import net.dreamlu.mica.core.utils.NumberUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.enums.PaymentAuditStatusEnum;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.ExamPlanMapper;
import top.continew.admin.exam.mapper.ExamineePaymentAuditMapper;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.dto.ExamPlanProjectPaymentDTO;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.query.ExamineePaymentAuditQuery;
import top.continew.admin.exam.model.req.ExamineePaymentAuditReq;
import top.continew.admin.exam.model.req.PaymentAuditConfirmReq;
import top.continew.admin.exam.model.req.PaymentInfoReq;
import top.continew.admin.exam.model.req.ReviewPaymentReq;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditDetailResp;
import top.continew.admin.exam.model.resp.ExamineePaymentAuditResp;
import top.continew.admin.exam.model.resp.PaymentInfoVO;
import top.continew.admin.exam.service.ExamineePaymentAuditService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.common.util.InMemoryMultipartFile;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamineePaymentAuditServiceImpl extends BaseServiceImpl<ExamineePaymentAuditMapper, ExamineePaymentAuditDO, ExamineePaymentAuditResp, ExamineePaymentAuditDetailResp, ExamineePaymentAuditQuery, ExamineePaymentAuditReq> implements ExamineePaymentAuditService {

    @Resource
    private ExamineePaymentAuditMapper examineePaymentAuditMapper;

    @Resource
    private ExamPlanMapper examPlanMapper;

    @Resource
    private ProjectMapper examProjectMapper;

    @Resource
    private UserMapper userMapper;

    private final ExcelUtilReactive excelUtilReactive;

    private final FileService fileService;

    @Resource
    private OrgClassMapper orgClassMapper;

    @Value("${excel.template.examination-payment-notice.inspector.url}")
    private String excelTemplateUrl;

    @Value("${excel.template.examination-payment-notice.worker.url}")
    private String workerExamNoticeTemplateUrl;

    @Value("${qrcode.worker.upload.payment-voucher.url}")
    private String qrcodeUrl;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private UploadService uploadService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private EnrollMapper enrollMapper;

    @Override
    public Boolean verifyPaymentAudit(Long examineeId) {
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        QueryWrapper<ExamineePaymentAuditDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("examinee_id", examineeId);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void generatePaymentAudit(Long examPlanId, Long examineeId, Long enrollId) throws Exception {
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        ValidationUtils.throwIfNull(enrollId, "报名ID不能为空");

        // 根据考试计划ID查询项目缴费金额
        ExamPlanProjectPaymentDTO paymentInfoDTO = getExamPlanProjectPaymentInfo(examPlanId);

        // 构建缴费审核记录并入库
        ExamineePaymentAuditDO paymentAuditDO = new ExamineePaymentAuditDO();
        paymentAuditDO.setExamineeId(examineeId);
        paymentAuditDO.setExamPlanId(examPlanId);
        paymentAuditDO.setEnrollId(enrollId);
        paymentAuditDO.setPaymentAmount(paymentInfoDTO.getPaymentAmount());
        paymentAuditDO.setNoticeNo(excelUtilReactive.generateUniqueNoticeNo(paymentInfoDTO.getProjectCode()));
        paymentAuditDO.setAuditStatus(0); // 待缴费状态
        paymentAuditDO.setCreateTime(LocalDateTime.now());
        paymentAuditDO.setIsDeleted(false);

        int insertCount = examineePaymentAuditMapper.insert(paymentAuditDO);
        ValidationUtils.throwIf(insertCount <= 0, "生成缴费审核记录失败");
    }

    @Override
    public PageResp<ExamineePaymentAuditResp> page(ExamineePaymentAuditQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamineePaymentAuditDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tepa.is_deleted", 0);
        IPage<ExamineePaymentAuditResp> page;
        if (query.getIsWorker()) {
            queryWrapper.isNotNull("tepa.class_id").isNotNull("tepa.qrcode_upload_url");
            page = baseMapper.getWorkerExamineePaymentAudits(new Page<>(pageQuery
                    .getPage(), pageQuery.getSize()), queryWrapper);
        } else {
            queryWrapper.isNull("tepa.class_id").isNull("tepa.qrcode_upload_url");
            page = baseMapper.getExamineePaymentAudits(new Page<>(pageQuery
                    .getPage(), pageQuery.getSize()), queryWrapper);
        }
        super.sort(queryWrapper, pageQuery);
        PageResp<ExamineePaymentAuditResp> pageResp = PageResp.build(page, super.getListClass());
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }

    @Override
    public Boolean uploadPaymentProof(ExamineePaymentAuditResp req) {
        // 参数校验
        if (req.getExamPlanId() == null || req.getExamineeId() == null) {
            throw new BusinessException("考试计划ID或考生ID不能为空！");
        }
        if (req.getPaymentProofUrl() == null || req.getPaymentProofUrl().isEmpty()) {
            throw new BusinessException("缴费凭证地址不能为空！");
        }
        // 查找当前记录
        ExamineePaymentAuditDO record = examineePaymentAuditMapper
            .selectOne(new LambdaQueryWrapper<ExamineePaymentAuditDO>().eq(ExamineePaymentAuditDO::getExamPlanId, req
                .getExamPlanId())
                .eq(ExamineePaymentAuditDO::getExamineeId, req.getExamineeId())
                .eq(ExamineePaymentAuditDO::getIsDeleted, false)
                .last("LIMIT 1"));
        if (record == null || record.getAuditNoticeUrl() == null) {
            throw new BusinessException("未找到缴费通知记录，不能上传凭证！");
        }
        Integer uploadStatus = req.getAuditStatus();  // 考生上传时传的状态
        if (uploadStatus == null) {
            throw new BusinessException("上传状态不能为空！");
        }
        Integer newStatus;
        switch (uploadStatus) {
            case 0:  // 待缴费
            case 1:  // 已缴费待审核
                newStatus = 1; // 上传后都变为已缴费待审核
                break;
            case 2:  // 审核通过
            case 5:  // 退款审核中
            case 7:  // 退款驳回
                newStatus = 5; // 上传后进入退款审核
                break;
            case 3:  // 审核驳回
            case 4:  // 补正审核
                newStatus = 4;
                break;
            case 6:  // 已退款
                throw new BusinessException("该考生已退款，禁止操作！");
            default:
                throw new BusinessException("未知的上传状态：" + uploadStatus);
        }
        // 更新记录
        record.setAuditStatus(newStatus);
        record.setPaymentProofUrl(req.getPaymentProofUrl());
        record.setPaymentTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        int result = examineePaymentAuditMapper.updateById(record);
        if (result <= 0) {
            throw new BusinessException("上传缴费凭证失败，请稍后再试！");
        }
        return true;
    }


    /**
     * 缴费审核资料
     *
     * @param req 审核请求对象
     * @return true=审核成功，false=失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewPayment(ReviewPaymentReq req) {

        // 1. 批量查询所有记录
        List<ExamineePaymentAuditDO> records = examineePaymentAuditMapper.selectList(
                new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                        .in(ExamineePaymentAuditDO::getId, req.getReviewIds())
                        .eq(ExamineePaymentAuditDO::getIsDeleted, false)
        );

        ValidationUtils.throwIfEmpty(records, "未找到任何需要审核的记录！");

        Integer newStatus = req.getAuditStatus();
        ValidationUtils.throwIf(newStatus == null || (!PaymentAuditStatusEnum.APPROVED.getValue().equals(newStatus)
                && !PaymentAuditStatusEnum.REJECTED.getValue().equals(newStatus)),"非法的审核状态！");

        Long auditorId = TokenLocalThreadUtil.get().getUserId();
        LocalDateTime now = LocalDateTime.now();

        //  只保存成功处理的记录
        List<ExamineePaymentAuditDO> successList = new ArrayList<>();

        for (ExamineePaymentAuditDO record : records) {

                Integer currentStatus = record.getAuditStatus();

                switch (currentStatus) {
                    case 0:
                        // 待缴费，跳过
                        continue;

                    case 1:
                        record.setAuditStatus(newStatus);
                        break;

                    case 2:
                        if (newStatus == 2) {
                            // 已审核通过且是通过请求，则跳过
                            continue;
                        }
                        record.setAuditStatus(3);
                        break;

                    case 3:
                        continue;
                    case 4:
                        record.setAuditStatus(newStatus);
                        break;

                    case 5:
                        // 退款审核逻辑可能失败，所以 try/catch 包裹
                        handleRefundAudit(record, newStatus);
                        break;
                    case 6:
                        // 已退款记录跳过
                        continue;
                    case 7:
                        if (newStatus == 2) {
                            record.setAuditStatus(6);
                        }
                        break;

                    default:
                        // 未知状态跳过
                        continue;
                }

                // 公共字段更新
                record.setRejectReason(req.getRejectReason());
                record.setAuditorId(auditorId);
                record.setAuditTime(now);
                successList.add(record);
        }

        // 3. 批量更新成功记录
        if (!successList.isEmpty()) {
            examineePaymentAuditMapper.updateBatchById(successList);
        }

        return true;
    }


    /**
     * 生成作业人员缴费通知单
     *
     * @param enrollDOList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatePaymentAudit(List<EnrollDO> enrollDOList) {
        if (ObjectUtil.isEmpty(enrollDOList)) {
            return;
        }
        // 根据考试计划ID查询项目缴费金额
        Long planId = enrollDOList.get(0).getExamPlanId();
        ExamPlanProjectPaymentDTO paymentInfoDTO = getExamPlanProjectPaymentInfo(planId);

        List<Long> classIds = enrollDOList.stream().map(EnrollDO::getClassId).distinct().toList();
        Map<Long, OrgClassDO> classMap = orgClassMapper.selectBatchIds(classIds)
            .stream()
            .collect(Collectors.toMap(OrgClassDO::getId, Function.identity()));

        // 构建缴费审核记录并入库
        List<ExamineePaymentAuditDO> insertList = enrollDOList.stream().map(item -> {
            ExamineePaymentAuditDO paymentAuditDO = new ExamineePaymentAuditDO();
            Long candidateId = item.getUserId();
            paymentAuditDO.setExamineeId(candidateId);
            paymentAuditDO.setExamPlanId(planId);
            Long enrollId = item.getId();
            paymentAuditDO.setEnrollId(enrollId);
            Long classId = item.getClassId();
            OrgClassDO orgClass = classMap.get(classId);
            paymentAuditDO.setClassId(classId);
            paymentAuditDO.setPaymentAmount(paymentInfoDTO.getPaymentAmount());
            String noticeNo = excelUtilReactive.generateUniqueNoticeNo(paymentInfoDTO.getProjectCode());
            paymentAuditDO.setNoticeNo(noticeNo);
            paymentAuditDO.setAuditStatus(0);
            paymentAuditDO.setCreateTime(LocalDateTime.now());
            paymentAuditDO.setIsDeleted(false);
            try {
                // 生成二维码内容
                String qrContent = buildQrContent(classId, candidateId, enrollId, planId);
                // 生成二维码并上传
                String qrUrl = generateAndUploadQr(candidateId, qrContent);
                paymentAuditDO.setQrcodeUploadUrl(qrUrl);
                byte[] photoBytes = loadPhotoSync(qrUrl);
                paymentAuditDO
                    .setAuditNoticeUrl(generateAuditNotice(planId, candidateId, paymentInfoDTO, noticeNo, orgClass
                        .getClassName(), photoBytes));
            } catch (Exception e) {
                throw new RuntimeException("生成或上传二维码失败");
            }
            return paymentAuditDO;
        }).toList();

        examineePaymentAuditMapper.insertBatch(insertList);
    }

    /**
     * 扫码查询作业人员缴费信息
     *
     * @param paymentInfoReq
     * @return
     */
    @Override
    public PaymentInfoVO getPaymentInfoByQrcode(PaymentInfoReq paymentInfoReq) {
        // 解密并转换为Long
        String planIdAes = aesWithHMAC.verifyAndDecrypt(paymentInfoReq.getPlanId());
        String candidateIdAes = aesWithHMAC.verifyAndDecrypt(paymentInfoReq.getCandidateId());
        String enrollIdAes = aesWithHMAC.verifyAndDecrypt(paymentInfoReq.getEnrollId());
        String classIdAes = aesWithHMAC.verifyAndDecrypt(paymentInfoReq.getClassId());

        // 参数完整性校验
        ValidationUtils.throwIf(ObjectUtil.isNull(planIdAes) || ObjectUtil.isNull(candidateIdAes) || ObjectUtil
            .isNull(enrollIdAes) || ObjectUtil.isNull(classIdAes), "二维码已被篡改或参数缺失，请重新获取");

        Long planId = NumberUtil.toLong(planIdAes);
        Long candidateId = NumberUtil.toLong(candidateIdAes);
        Long enrollId = NumberUtil.toLong(enrollIdAes);
        Long classId = NumberUtil.toLong(classIdAes);

        // 查询报名信息
        EnrollDO enrollDO = enrollMapper.selectById(enrollId);
        ValidationUtils.throwIfNull(enrollDO, "未找到报名信息");

        // 查询缴费记录
        ExamineePaymentAuditDO auditDO = baseMapper.selectOne(new LambdaQueryWrapper<ExamineePaymentAuditDO>()
            .eq(ExamineePaymentAuditDO::getExamPlanId, planId)
            .eq(ExamineePaymentAuditDO::getExamineeId, candidateId)
            .eq(ExamineePaymentAuditDO::getEnrollId, enrollId)
            .eq(ExamineePaymentAuditDO::getClassId, classId));
        ValidationUtils.throwIfNull(auditDO, "未找到报名信息");

        // 查询个人信息
        PaymentInfoVO paymentInfoVO = baseMapper.selectPaymentPersonInfo(candidateId, classId);
        ValidationUtils.throwIfNull(paymentInfoVO, "未找到报名信息");

        // 合并缴费记录
        BeanUtil.copyProperties(auditDO, paymentInfoVO);

        return paymentInfoVO;
    }

    /**
     * 扫码确认提交作业人员缴费信息
     *
     * @param paymentAuditConfirmReq
     * @return
     */
    @Override
    public Boolean paymentAuditConfirm(PaymentAuditConfirmReq paymentAuditConfirmReq) {
        ExamineePaymentAuditDO examineePaymentAuditDO = baseMapper.selectById(paymentAuditConfirmReq.getId());
        ValidationUtils.throwIfEmpty(examineePaymentAuditDO, "未找到报名信息");
        // 不能重复提交待审核状态
        Integer PAID_PENDING_REVIEW = PaymentAuditStatusEnum.PAID_PENDING_REVIEW.getValue();
        ValidationUtils.throwIf(PAID_PENDING_REVIEW.equals(paymentAuditConfirmReq
            .getAuditStatus()) && PAID_PENDING_REVIEW.equals(examineePaymentAuditDO
                .getAuditStatus()), "您已提交过缴费凭证，请勿重复提交！");
        return baseMapper.update(new LambdaUpdateWrapper<ExamineePaymentAuditDO>()
            .set(ExamineePaymentAuditDO::getAuditStatus, paymentAuditConfirmReq.getAuditStatus())
            .set(ExamineePaymentAuditDO::getPaymentTime, LocalDateTime.now())
            .set(ExamineePaymentAuditDO::getPaymentProofUrl, paymentAuditConfirmReq.getPaymentProofUrl())
            .set(ExamineePaymentAuditDO::getRejectReason, null)
            .eq(ExamineePaymentAuditDO::getId, examineePaymentAuditDO.getId())) > 0;
    }

    /**
     * 将图片URL转成字节流
     *
     * @param photoUrl
     * @return
     */
    private byte[] loadPhotoSync(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            log.warn("照片 URL 为空，返回空字节数组");
            return new byte[0];
        }
        byte[] photoBytes = restTemplate.getForObject(photoUrl, byte[].class);
        return photoBytes != null ? photoBytes : new byte[0];
    }

    /**
     * 构建二维码内容（带加密）
     */
    private String buildQrContent(Long classId, Long candidateId, Long enrollId, Long planId) {
        Map<String, Long> params = Map
            .of("classId", classId, "candidateId", candidateId, "enrollId", enrollId, "planId", planId);

        String query = params.entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + encode(aesWithHMAC.encryptAndSign(String.valueOf(e.getValue()))))
            .collect(Collectors.joining("&"));

        return qrcodeUrl + "?" + query;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 生成二维码并上传，返回 URL
     */
    private String generateAndUploadQr(Long candidateId, String qrContent) throws IOException {

        // 1. 生成二维码（300x300）
        BufferedImage qrImage = QrCodeUtil.generate(qrContent, 300, 300);

        // 2. 转为字节流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        // 3. 封装成 MultipartFile
        MultipartFile file = new InMemoryMultipartFile("file", candidateId + ".png", "image/png", bytes);

        // 4. 上传文件
        GeneralFileReq req = new GeneralFileReq();
        req.setType("pic");

        FileInfoResp resp = uploadService.upload(file, req);

        return resp.getUrl();
    }

    /**
     * 处理退款审核逻辑（状态5 -> 状态6）
     *
     * @param record    当前审核记录
     * @param newStatus 提交的新审核状态
     */
    private void handleRefundAudit(ExamineePaymentAuditDO record, Integer newStatus) {
        if (newStatus == 2) {
            record.setAuditStatus(6); // 已退款
            // ... 业务待定
        } else  {
            record.setAuditStatus(7); // 退款驳回
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamineePaymentAuditDO getByExamPlanIdAndExamineeId(Long examPlanId, Long examineeId) {
        // 先查找当前记录
        ExamineePaymentAuditDO record = examineePaymentAuditMapper
            .selectOne(new LambdaQueryWrapper<ExamineePaymentAuditDO>()
                .eq(ExamineePaymentAuditDO::getExamPlanId, examPlanId)
                .eq(ExamineePaymentAuditDO::getExamineeId, examineeId)
                .eq(ExamineePaymentAuditDO::getIsDeleted, false)
                .last("LIMIT 1"));

        if (record == null) {
            throw new IllegalStateException("未找到对应的缴费审核记录，请检查考试计划和考生信息。");
        }

        // 如果已存在通知单 URL，则直接返回该记录
        if (record.getAuditNoticeUrl() != null) {
            return record;
        }
        // 否则生成通知单 PDF
        // 获取缴费金额等信息
        ExamPlanProjectPaymentDTO paymentInfoDTO = getExamPlanProjectPaymentInfo(examPlanId);
        if (paymentInfoDTO == null) {
            throw new IllegalStateException("未找到考试计划缴费信息: " + examPlanId);
        }
        String auditNoticeUrl = generateAuditNotice(examPlanId, examineeId, paymentInfoDTO, record
            .getNoticeNo(), null, new byte[0]);

        // 更新当前记录
        record.setAuditNoticeUrl(auditNoticeUrl);
        examineePaymentAuditMapper.updateById(record);

        // 返回更新后的记录（此处 record 已包括新 URL）
        return record;
    }

    /**
     * 生成通知单pdf
     *
     * @param examPlanId
     * @param examineeId
     * @param paymentInfoDTO
     * @param noticeNo
     * @return
     */
    private String generateAuditNotice(Long examPlanId,
                                       Long examineeId,
                                       ExamPlanProjectPaymentDTO paymentInfoDTO,
                                       String noticeNo,
                                       String className,
                                       byte[] photoBytes) {
        // 生成 PDF
        byte[] pdfBytes = generatePaymentNotice(examPlanId, examineeId, paymentInfoDTO.getExamPlanName(), paymentInfoDTO
            .getProjectName(), paymentInfoDTO.getPaymentAmount().longValue(), noticeNo, className, photoBytes);

        // 封装为 MultipartFile
        String nickname = userMapper.selectById(examineeId).getNickname();
        MultipartFile pdfFile = new InMemoryMultipartFile("file", nickname + "_缴费通知单.pdf", "application/pdf", pdfBytes);

        // 上传 OSS
        FileInfoResp fileInfoResp = fileService.upload(pdfFile, new GeneralFileReq());
        if (fileInfoResp == null || fileInfoResp.getUrl() == null) {
            throw new IllegalStateException("文件上传失败");
        }
        String pdfUrl = fileInfoResp.getUrl();
        return pdfUrl;
    }

    @Override
    public byte[] generatePaymentNotice(Long examPlanId,
                                        Long examineeId,
                                        String examPlanName,
                                        String projectName,
                                        Long paymentAmount,
                                        String noticeNo,
                                        String className,
                                        byte[] photoBytes) {
        ValidationUtils.throwIfNull(examPlanId, "考试计划ID不能为空");
        ValidationUtils.throwIfNull(examineeId, "考生ID不能为空");
        ValidationUtils.throwIfNull(examPlanName, "考试名称不能为空");
        ValidationUtils.throwIfNull(projectName, "收费项目不能为空");
        ValidationUtils.throwIfNull(paymentAmount, "缴费金额不能为空");

        boolean isWorker = ObjectUtil.isNotEmpty(className);

        String candidateName = userMapper.selectNicknameById(examineeId);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("noticeNo", excelUtilReactive.getSafeValue(noticeNo));
        dataMap.put("candidateName", excelUtilReactive.getSafeValue(candidateName));
        dataMap.put("examPlanName", excelUtilReactive.getSafeValue(examPlanName));
        dataMap.put("projectName", excelUtilReactive.getSafeValue(projectName));
        dataMap.put("paymentAmount", excelUtilReactive.getSafeValue(String.valueOf(paymentAmount)));
        if (isWorker) {
            dataMap.put("applyClassName", excelUtilReactive.getSafeValue(String.valueOf(className)));
        }
        dataMap.putAll(excelUtilReactive.splitAmountToUpper(BigDecimal.valueOf(paymentAmount.intValue())));
        // 阻塞生成 PDF
        if (isWorker) {
            return excelUtilReactive
                .generatePdfBytesSync(dataMap, workerExamNoticeTemplateUrl, photoBytes, 7, 8, 21, 22);
        } else {
            return excelUtilReactive.generatePdfBytesSync(dataMap, excelTemplateUrl, photoBytes, 3, 3, 1, 5);
        }
    }

    // 根据考试计划ID查询项目缴费金额
    private ExamPlanProjectPaymentDTO getExamPlanProjectPaymentInfo(Long examPlanId) {
        // 查询考试计划
        ExamPlanDO examPlanDO = examPlanMapper.selectOne(new LambdaQueryWrapper<ExamPlanDO>()
            .eq(ExamPlanDO::getId, examPlanId)
            .eq(ExamPlanDO::getIsDeleted, false));
        ValidationUtils.throwIfNull(examPlanDO, "考试计划不存在");

        // 获取关联项目ID和考试计划名称
        Long examProjectId = examPlanDO.getExamProjectId();
        String examPlanName = examPlanDO.getExamPlanName(); // 提取考试计划名称
        ValidationUtils.throwIfNull(examProjectId, "考试计划未关联项目ID");
        ValidationUtils.throwIfBlank(examPlanName, "考试计划名称不能为空"); // 额外校验名称非空

        //  查询项目信息
        ProjectDO projectDO = examProjectMapper.selectOne(new LambdaQueryWrapper<ProjectDO>()
            .eq(ProjectDO::getId, examProjectId)
            .eq(ProjectDO::getIsDeleted, false));
        ValidationUtils.throwIfNull(projectDO, "考试项目不存在");

        // 提取项目名称和缴费金额
        String projectName = projectDO.getProjectName();
        BigDecimal paymentAmount = BigDecimal.valueOf(projectDO.getExamFee());
        ValidationUtils.throwIfBlank(projectName, "项目名称不能为空");
        ValidationUtils.throwIfNull(paymentAmount, "项目未设置缴费金额");

        return new ExamPlanProjectPaymentDTO(examPlanName, projectName, paymentAmount, projectDO.getProjectCode());
    }
}
