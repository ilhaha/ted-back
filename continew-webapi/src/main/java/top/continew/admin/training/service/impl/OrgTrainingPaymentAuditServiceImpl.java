package top.continew.admin.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.ExamineePaymentAuditDO;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.training.mapper.OrgCandidateMapper;
import top.continew.admin.training.mapper.OrgMapper;
import top.continew.admin.training.model.entity.OrgCandidateDO;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.util.ExcelUtilReactive;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgTrainingPaymentAuditMapper;
import top.continew.admin.training.model.entity.OrgTrainingPaymentAuditDO;
import top.continew.admin.training.model.query.OrgTrainingPaymentAuditQuery;
import top.continew.admin.training.model.req.OrgTrainingPaymentAuditReq;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPaymentAuditResp;
import top.continew.admin.training.service.OrgTrainingPaymentAuditService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）业务实现
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Service
@RequiredArgsConstructor
public class OrgTrainingPaymentAuditServiceImpl extends BaseServiceImpl<OrgTrainingPaymentAuditMapper, OrgTrainingPaymentAuditDO, OrgTrainingPaymentAuditResp, OrgTrainingPaymentAuditDetailResp, OrgTrainingPaymentAuditQuery, OrgTrainingPaymentAuditReq> implements OrgTrainingPaymentAuditService {

    @Resource
    private OrgTrainingPaymentAuditMapper orgTrainingPaymentAuditMapper;


    @Resource
    private ExcelUtilReactive excelUtilReactive;


    @Resource
    private OrgMapper orgMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private FileService fileService;


    @Resource
    private OrgCandidateMapper orgCandidateMapper;

    @Value("${excel.template.training-payment-notice.inspector.url}")
    private String excelTemplateUrl;


    @Override
    public OrgTrainingPaymentAuditDO getByTrainingOrgIdAndEnrollId(Long orgId, Long enrollId) {
        // 先查找当前记录
        OrgTrainingPaymentAuditDO record = orgTrainingPaymentAuditMapper.selectOne(
                new LambdaQueryWrapper<OrgTrainingPaymentAuditDO>()
                        .eq(OrgTrainingPaymentAuditDO::getOrgId, orgId)
                        .eq(OrgTrainingPaymentAuditDO::getEnrollId, enrollId)
                        .eq(OrgTrainingPaymentAuditDO::getIsDeleted, false)
                        .last("LIMIT 1")
        );
        if (record == null) {
            throw new IllegalStateException("未找到对应的缴费审核记录，请检查考试计划和考生信息。");
        }
        // 如果已存在通知单 URL，则直接返回该记录
        if (record.getAuditNoticeUrl() != null) {
            return record;
        }
        //查询需要生成pdf的字段
        OrgDO orgDO = orgMapper.selectById(orgId);
        ProjectDO projectDO = projectMapper.selectById(record.getProjectId());
        // 否则生成通知单 PDF
        String auditNoticeUrl = generateAuditNotice(orgDO.getName(),projectDO.getProjectName(),record.getNoticeNo(),record.getPaymentAmount());

        // 更新当前记录
        record.setAuditNoticeUrl(auditNoticeUrl);
        orgTrainingPaymentAuditMapper.updateById(record);

        // 返回更新后的记录（此处 record 已包括新 URL）
        return record;

    }


    private String generateAuditNotice(String orgName, String projectName, String noticeNo, BigDecimal paymentAmount) {
        // 生成 PDF
        byte[] pdfBytes = generateTrainingPaymentNotice(
                orgName,
                projectName,
                noticeNo,
                paymentAmount
        );

        // 封装为 MultipartFile
        MultipartFile pdfFile = new InMemoryMultipartFile(
                "file",
                TokenLocalThreadUtil.get().getNickname() + "_缴费通知单.pdf",
                "application/pdf",
                pdfBytes
        );

        // 上传 OSS
        FileInfoResp fileInfoResp = fileService.upload(pdfFile, new GeneralFileReq());
        if (fileInfoResp == null || fileInfoResp.getUrl() == null) {
            throw new IllegalStateException("文件上传失败");
        }
        String pdfUrl = fileInfoResp.getUrl();
        return pdfUrl;
    }


    @Override
    public byte[] generateTrainingPaymentNotice(String orgName, String projectName, String noticeNo, BigDecimal paymentAmount) {
        ValidationUtils.throwIfNull(orgName, "机构名称不能为空");
        ValidationUtils.throwIfNull(noticeNo, "培训缴费通知单不能为空");
        ValidationUtils.throwIfNull(projectName, "考试项目不能为空");
        ValidationUtils.throwIfNull(paymentAmount, "缴费金不能为空");

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("noticeNo", excelUtilReactive.getSafeValue(noticeNo));
        dataMap.put("candidateName", excelUtilReactive.getSafeValue(TokenLocalThreadUtil.get().getNickname()));
        dataMap.put("orgName", excelUtilReactive.getSafeValue(orgName));
        dataMap.put("projectName", excelUtilReactive.getSafeValue(projectName));
        dataMap.put("paymentAmount", excelUtilReactive.getSafeValue(String.valueOf(paymentAmount)));
        dataMap.put("paymentDateTime", LocalDateTime.now());
        dataMap.putAll(excelUtilReactive.splitAmountToUpper(BigDecimal.valueOf(paymentAmount.intValue())));
        // 阻塞生成 PDF
        return excelUtilReactive.generatePdfBytesSync(dataMap, excelTemplateUrl , new byte[0],0,0,0,0);
    }

    @Override
    public Boolean uploadTrainingPaymentProof(OrgTrainingPaymentAuditResp req) {
        // 参数校验
        if (req.getOrgId() == null || req.getEnrollId() == null) {
            throw new BusinessException("机构或申请加入机构申请不能为空！");
        }
        if (req.getPaymentProofUrl() == null || req.getPaymentProofUrl().isEmpty()) {
            throw new BusinessException("缴费凭证地址不能为空！");
        }
        // 查找当前记录
        OrgTrainingPaymentAuditDO record = orgTrainingPaymentAuditMapper.selectOne(
                new LambdaQueryWrapper<OrgTrainingPaymentAuditDO>()
                        .eq(OrgTrainingPaymentAuditDO::getOrgId, req.getOrgId())
                        .eq(OrgTrainingPaymentAuditDO::getEnrollId, req.getEnrollId())
                        .eq(OrgTrainingPaymentAuditDO::getIsDeleted, false)
                        .last("LIMIT 1")
        );
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

        int result = orgTrainingPaymentAuditMapper.updateById(record);

        // 更新 org_candidate 表，状态和 record 完全同步（用 newStatus）
        OrgCandidateDO candidateDO = new OrgCandidateDO();
        candidateDO.setId(req.getEnrollId());
        candidateDO.setPaymentStatus(newStatus); // 和 record 的最终状态保持一致
        candidateDO.setUpdateTime(LocalDateTime.now());

        int candidateResult = orgCandidateMapper.updateById(candidateDO);
        if (candidateResult <= 0) {
            throw new BusinessException("更新考生状态失败（与缴费审核状态同步）！");
        }

        // 校验主表更新结果
        if (result <= 0) {
            throw new BusinessException("上传缴费凭证失败，请稍后再试！");
        }
        return true;

    }

}