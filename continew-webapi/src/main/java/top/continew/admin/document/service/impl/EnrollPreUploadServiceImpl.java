package top.continew.admin.document.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.document.mapper.DocumentMapper;
import top.continew.admin.document.mapper.DocumentPreMapper;
import top.continew.admin.document.mapper.ExamineeDocumentMapper;
import top.continew.admin.document.model.dto.DocFileDTO;
import top.continew.admin.document.model.dto.EnrollPrePassDTO;
import top.continew.admin.document.model.entity.DocumentDO;
import top.continew.admin.document.model.entity.DocumentPreDO;
import top.continew.admin.document.model.entity.ExamineeDocumentDO;
import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.admin.document.model.req.EnrollPreReviewReq;
import top.continew.admin.exam.mapper.EnrollMapper;
import top.continew.admin.exam.mapper.SpecialCertificationApplicantMapper;
import top.continew.admin.exam.model.entity.EnrollDO;
import top.continew.admin.exam.model.entity.SpecialCertificationApplicantDO;
import top.continew.admin.training.mapper.EnrollPreMapper;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.model.entity.EnrollPreDO;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.document.mapper.EnrollPreUploadMapper;
import top.continew.admin.document.model.entity.EnrollPreUploadDO;
import top.continew.admin.document.model.query.EnrollPreUploadQuery;
import top.continew.admin.document.model.req.EnrollPreUploadReq;
import top.continew.admin.document.model.resp.EnrollPreUploadDetailResp;
import top.continew.admin.document.model.resp.EnrollPreUploadResp;
import top.continew.admin.document.service.EnrollPreUploadService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 机构报考-考生扫码上传文件业务实现
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Service
@RequiredArgsConstructor
public class EnrollPreUploadServiceImpl extends BaseServiceImpl<EnrollPreUploadMapper, EnrollPreUploadDO, EnrollPreUploadResp, EnrollPreUploadDetailResp, EnrollPreUploadQuery, EnrollPreUploadReq> implements EnrollPreUploadService {

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private DocumentPreMapper documentPreMapper;

    @Resource
    private OrgClassCandidateMapper orgClassCandidateMapper;

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private EnrollMapper enrollMapper;

    @Resource
    private SpecialCertificationApplicantMapper specialCertificationApplicantMapper;

    @Resource
    private ExamineeDocumentMapper examineeDocumentMapper;

    @Resource
    private EnrollPreMapper enrollPreMapper;

    /**
     * 通过二维码上传上传考生资料
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean qrcodeUpload(QrcodeUploadReq qrcodeUploadReq) {
        // 首先判断是否是本人扫码
        String aesPlanId = aesWithHMAC.verifyAndDecrypt(qrcodeUploadReq.getPlanId());
        String aesCandidateId = aesWithHMAC.verifyAndDecrypt(qrcodeUploadReq.getCandidateId());

        // 校验二维码有效性
        ValidationUtils.throwIf(
                ObjectUtil.isEmpty(aesPlanId) || ObjectUtil.isEmpty(aesCandidateId),
                "二维码信息已失效，请重新获取"
        );

        Long planId = Long.valueOf(aesPlanId);
        Long candidateId = Long.valueOf(aesCandidateId);
        // 获取用户信息并验证身份证
        String aesUsername = aesWithHMAC.verifyAndDecrypt(baseMapper.getUsernameById(candidateId));
        String idLastSix = aesUsername.substring(aesUsername.length() - 6);

        // 身份验证
        ValidationUtils.throwIf(
                !qrcodeUploadReq.getIdLastSix().equals(idLastSix),
                "身份信息有误，请确认信息后重新输入"
        );
        // 查询考生正在所在班级
        LambdaQueryWrapper<OrgClassCandidateDO> orgClassCandidateDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orgClassCandidateDOLambdaQueryWrapper.eq(OrgClassCandidateDO::getCandidateId, candidateId)
                .eq(OrgClassCandidateDO::getStatus, 0);
        OrgClassCandidateDO orgClassCandidateDO = orgClassCandidateMapper.selectOne(orgClassCandidateDOLambdaQueryWrapper);

        EnrollPreUploadDO enrollPreUploadDO = new EnrollPreUploadDO();
        enrollPreUploadDO.setPlanId(planId);
        enrollPreUploadDO.setCandidatesId(candidateId);
        enrollPreUploadDO.setQualificationFileUrl(qrcodeUploadReq.getQualificationFileUrl());
        enrollPreUploadDO.setStatus(0);
        enrollPreUploadDO.setBatchId(orgClassCandidateDO.getClassId());
        enrollPreUploadDO.setCreateUser(candidateId);
        int row = baseMapper.insert(enrollPreUploadDO);
        List<DocFileDTO> docFileList = qrcodeUploadReq.getDocFileList();
        if (ObjectUtil.isNotEmpty(docFileList)) {
            List<DocumentPreDO> insertDocList = new ArrayList<>();
            qrcodeUploadReq.getDocFileList().stream().forEach(docFile -> {
                for (String url : docFile.getUrls()) {
                    DocumentPreDO documentPreDO = new DocumentPreDO();
                    documentPreDO.setTypeId(docFile.getTypeId());
                    documentPreDO.setDocPath(url);
                    documentPreDO.setCreateUser(candidateId);
                    documentPreDO.setEnrollPreUploadId(enrollPreUploadDO.getId());
                    insertDocList.add(documentPreDO);
                }
            });
            documentPreMapper.insertBatch(insertDocList);
        }
        return row > 0;
    }

    /**
     * 机构报考
     *
     * @param reviewReq
     * @return
     */
    @Override
    @Transactional
    public Boolean review(EnrollPreReviewReq reviewReq) {
        // 审核原因校验：退回补正或虚假材料时必须填写 remark
        ValidationUtils.throwIf(
                (reviewReq.getStatus().equals(2) || reviewReq.getStatus().equals(3))
                        && ObjectUtil.isEmpty(reviewReq.getRemark()),
                "请填写审核原因"
        );

        // 更新审核状态和备注
        LambdaUpdateWrapper<EnrollPreUploadDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(EnrollPreUploadDO::getStatus, reviewReq.getStatus())
                .set(EnrollPreUploadDO::getRemark, reviewReq.getRemark())
                .in(EnrollPreUploadDO::getId, reviewReq.getReviewIds());

        //  审核通过才执行生成报名信息逻辑
        if (Integer.valueOf(1).equals(reviewReq.getStatus())) {
            List<EnrollPrePassDTO> enrollPrePassDTOS = baseMapper.selectPreDoc(reviewReq.getReviewIds());

            if (CollUtil.isNotEmpty(enrollPrePassDTOS)) {
                List<DocumentDO> documentList = new ArrayList<>();
                List<SpecialCertificationApplicantDO> applicantList = new ArrayList<>();
                List<EnrollDO> enrollList = new ArrayList<>();
                // 2. 用 Map 记录已处理过的 candidatesId
                Set<String> handledCandidates = new HashSet<>();

                for (EnrollPrePassDTO dto : enrollPrePassDTOS) {
                    // 1. 生成 DocumentDO
                    DocumentDO doc = new DocumentDO();
                    doc.setTypeId(dto.getTypeId());
                    doc.setDocPath(dto.getDocPaths());
                    doc.setStatus(1);
                    doc.setCreateUser(dto.getCandidatesId());
                    doc.setCreateTime(LocalDateTime.now());
                    doc.setUpdateUser(dto.getCandidatesId());
                    doc.setUpdateTime(LocalDateTime.now());
                    documentList.add(doc);

                    String key = dto.getId() + "_" + dto.getCandidatesId() + "_" + dto.getPlanId();
                    //  EnrollDO & SpecialCertificationApplicantDO 仅首次生成
                    if (!handledCandidates.contains(key)) {
                        handledCandidates.add(key);

                        EnrollDO enrollDO = new EnrollDO();
                        enrollDO.setUserId(dto.getCandidatesId());
                        enrollDO.setExamPlanId(dto.getPlanId());
                        enrollDO.setEnrollStatus(1L);
                        enrollDO.setCreateUser(dto.getCandidatesId());
                        enrollDO.setCreateTime(LocalDateTime.now());
                        enrollDO.setUpdateUser(dto.getCandidatesId());
                        enrollDO.setUpdateTime(LocalDateTime.now());
                        enrollList.add(enrollDO);

                        SpecialCertificationApplicantDO applicantDO = new SpecialCertificationApplicantDO();
                        applicantDO.setCandidatesId(dto.getCandidatesId());
                        applicantDO.setPlanId(dto.getPlanId());
                        applicantDO.setBatchId(dto.getBatchId());
                        applicantDO.setImageUrl(dto.getQualificationFileUrl());
                        applicantDO.setStatus(1);
                        applicantDO.setApplySource(0);
                        applicantDO.setCreateUser(dto.getCandidatesId());
                        applicantDO.setCreateTime(LocalDateTime.now());
                        applicantDO.setUpdateUser(dto.getCandidatesId());
                        applicantDO.setUpdateTime(LocalDateTime.now());
                        applicantList.add(applicantDO);
                    }
                }
                // 4 批量插入数据
                if (CollUtil.isNotEmpty(documentList)) {
                    // 收集 (candidateId, typeId) 对
                    List<Long> candidateIds = new ArrayList<>();
                    List<Long> typeIds = new ArrayList<>();
                    Set<String> pairSet = new HashSet<>();

                    for (DocumentDO doc : documentList) {
                        String key = doc.getCreateUser() + "_" + doc.getTypeId();
                        if (pairSet.add(key)) {
                            candidateIds.add(doc.getCreateUser());
                            typeIds.add(doc.getTypeId());
                        }
                    }

                    // 一次性批量删除
                    documentMapper.delete(
                            new LambdaQueryWrapper<DocumentDO>()
                                    .in(DocumentDO::getCreateUser, candidateIds)
                                    .in(DocumentDO::getTypeId, typeIds)
                                    .ne(DocumentDO::getStatus, 1)
                    );

                    // 再批量插入
                    documentMapper.insertBatch(documentList);
                    // 再插入考生资料表
                    List<ExamineeDocumentDO> examineeDocumentDOS = documentList.stream().map(documentDO -> {
                        ExamineeDocumentDO examineeDocumentDO = new ExamineeDocumentDO();
                        examineeDocumentDO.setDocumentId(documentDO.getId());
                        examineeDocumentDO.setExamineeId(documentDO.getCreateUser());
                        examineeDocumentDO.setCreateUser(documentDO.getCreateUser());
                        examineeDocumentDO.setCreateTime(LocalDateTime.now());
                        examineeDocumentDO.setUpdateUser(documentDO.getCreateUser());
                        examineeDocumentDO.setUpdateTime(LocalDateTime.now());
                        return examineeDocumentDO;
                    }).toList();
                    examineeDocumentMapper.insertBatch(examineeDocumentDOS);
                }

                if (CollUtil.isNotEmpty(enrollList)) {
                    enrollMapper.insert(enrollList);
                }
                if (CollUtil.isNotEmpty(applicantList)) {
                    // 批量插入申请人信息
                    specialCertificationApplicantMapper.insert(applicantList);

                    // 取出所有 candidateId-planId 组合
                    Set<String> candidatePlanPairs = applicantList.stream()
                            .map(a -> a.getCandidatesId() + "_" + a.getPlanId())
                            .collect(Collectors.toSet());

                    // 批量更新预报名表
                    for (String pair : candidatePlanPairs) {
                        String[] parts = pair.split("_");
                        Long candidateId = Long.valueOf(parts[0]);
                        Long planId = Long.valueOf(parts[1]);

                        LambdaUpdateWrapper<EnrollPreDO> enrollPreDOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                        enrollPreDOLambdaUpdateWrapper
                                .eq(EnrollPreDO::getCandidateId, candidateId)
                                .eq(EnrollPreDO::getPlanId, planId)
                                .set(EnrollPreDO::getStatus, 1);

                        enrollPreMapper.update(null, enrollPreDOLambdaUpdateWrapper);
                    }
                }

            }
        }
        return this.update(updateWrapper);
    }


    /**
     * 重写分页查询
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<EnrollPreUploadResp> page(EnrollPreUploadQuery query, PageQuery pageQuery) {
        QueryWrapper<EnrollPreUploadDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tepu.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<EnrollPreUploadDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<EnrollPreUploadResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}