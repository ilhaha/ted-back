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

package top.continew.admin.training.service.impl;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.OrgClassType;
import top.continew.admin.common.constant.SelectClassConstants;
import top.continew.admin.common.constant.enums.*;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.DownloadOSSFileUtil;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.mapper.WeldingExamApplicationMapper;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.exam.model.entity.WeldingExamApplicationDO;
import top.continew.admin.exam.model.req.ReviewPaymentReq;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.admin.training.model.req.OrgClassPaymentUpdateReq;
import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.admin.common.util.InMemoryMultipartFile;
import top.continew.admin.worker.mapper.WorkerApplyMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDO;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgClassMapper;
import top.continew.admin.training.model.entity.OrgClassDO;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;
import top.continew.admin.training.service.OrgClassService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 培训机构班级业务实现
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Service
@RequiredArgsConstructor
public class OrgClassServiceImpl extends BaseServiceImpl<OrgClassMapper, OrgClassDO, OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> implements OrgClassService {

    @Resource
    private OrgUserMapper orgUserMapper;

    @Value("${qrcode.worker.upload.apply-doc.url}")
    private String qrcodeUrl;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private UploadService uploadService;

    private final ProjectMapper projectMapper;

    private final WorkerApplyMapper workerApplyMapper;

    private final OrgClassCandidateMapper orgClassCandidateMapper;

    private final WeldingExamApplicationMapper weldingExamApplicationMapper;

    @Value("${welding.metal-project-id}")
    private Long metalProjectId;

    @Value("${welding.nonmetal-project-id}")
    private Long nonmetalProjectId;

    /**
     * 重写分页查询
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<OrgClassResp> page(OrgClassQuery query, PageQuery pageQuery) {
        IPage<OrgClassDetailResp> page;
        QueryWrapper<OrgClassDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("toc.is_deleted", 0);
        // 机构查询
        if (Boolean.TRUE.equals(query.getIsOrgQuery())) {
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());
            queryWrapper.eq("toc.org_id", orgDO.getId());
            super.sort(queryWrapper, pageQuery);
            if (OrgClassType.INSPECTORS_TYPE.getClassType().equals(query.getClassType())) {
                page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
            } else {
                page = baseMapper.workerClassPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
            }
            // 后台查询
        } else {
            Integer flag = query.getFlag();
            if (AuditQueryFlagEnum.APPLY_AUDIT.getCode().equals(flag)) {
                page = baseMapper.adminQueryWorkerClassPage(new Page<>(pageQuery.getPage(), pageQuery
                        .getSize()), queryWrapper);

            } else {
                page = baseMapper.adminQueryPayAuditPage(new Page<>(pageQuery.getPage(), pageQuery
                        .getSize()), queryWrapper);
            }
        }
        PageResp<OrgClassResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 重写添加
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(OrgClassReq req) {
        // 当前用户与所属机构
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());
        if (orgDO == null) {
            throw new BusinessException("当前用户未绑定机构");
        }
        // 查出项目考试价格
        Long projectId = req.getProjectId();
        ProjectDO projectDO = projectMapper.selectById(projectId);
        ValidationUtils.throwIfNull(projectDO, "未查询到考试项目信息");
        req.setPayStatus(projectDO.getExamFee() == 0L
                ? OrgClassPayStatusEnum.FREE.getCode()
                : OrgClassPayStatusEnum.UNPAID.getCode());

        // 设置机构ID
        Long orgId = orgDO.getId();
        req.setOrgId(orgId);
        // 判断是否添加的是焊接项目的班级
//        boolean isWelding = Objects.equals(projectId, metalProjectId) || Objects.equals(projectId, nonmetalProjectId);
//
//        if (isWelding) {
//            // 判断焊接类型
//            WeldingTypeEnum weldingTypeEnum = Objects.equals(projectId, metalProjectId)
//                ? WeldingTypeEnum.METAL
//                : WeldingTypeEnum.NON_METAL;
//
//            Long count = weldingExamApplicationMapper.selectCount(new LambdaQueryWrapper<WeldingExamApplicationDO>()
//                .eq(WeldingExamApplicationDO::getOrgId, orgId)
//                .eq(WeldingExamApplicationDO::getStatus, WeldingExamApplicationStatusEnum.PASS_REVIEW.getValue())
//                .eq(WeldingExamApplicationDO::getWeldingType, weldingTypeEnum.getValue()));
//
//            ValidationUtils.throwIf(count == null || count == 0, "机构下未有申报通过的" + weldingTypeEnum.getDesc() + "项目");
//        }

        // 循环重试生成班级编号并插入
        Long classId = null;
        int retry = 0;
        final int maxRetry = 5;
        while (retry < maxRetry) {
            retry++;

            // 生成班级编号
            String className = generateClassCode(req, orgDO.getCode(), orgId);
            req.setClassName(className);

            try {
                // 尝试插入班级
                classId = super.add(req);
                break; // 成功则跳出循环
            } catch (Exception e) {
                // 班级编号重复，重试
                if (retry >= maxRetry) {
                    throw new BusinessException("班级编号生成失败，请稍后重试");
                }
            }
        }

        // 如果是作业人员班级，生成二维码
        if (OrgClassType.WORKER_TYPE.getClassType().equals(req.getClassType())) {
            try {
                String qrContent = buildQrContent(classId);
                String qrUrl = generateAndUploadQr(classId, qrContent);

                // 更新二维码地址
                baseMapper.update(new LambdaUpdateWrapper<OrgClassDO>().eq(OrgClassDO::getId, classId)
                        .set(OrgClassDO::getQrcodeApplyUrl, qrUrl));
            } catch (Exception e) {
                throw new BusinessException("二维码生成失败，请稍后重试");
            }
        }

        return classId;
    }

    /**
     * 生成班级编号
     * 规则：考试类型 + 年份(两位) + 学校代号 + 项目编码 + 班级序号(三位)
     */
    private String generateClassCode(OrgClassReq req, String orgCode, Long orgId) {
        // 考试类型
        String examType = OrgClassType.WORKER_TYPE.getClassType().equals(req.getClassType()) ? "K" : "P";

        // 年份两位
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));

        // 校验学校代号
        if (orgCode == null || orgCode.isEmpty())
            orgCode = "00";

        // 项目
        ProjectDO projectDO = projectMapper.selectById(req.getProjectId());
        if (projectDO == null)
            throw new BusinessException("项目不存在");

        // 统计今年同类型同项目班级数量
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<OrgClassDO>().eq(OrgClassDO::getClassType, req
                        .getClassType())
                .eq(OrgClassDO::getProjectId, projectDO.getId())
                .eq(OrgClassDO::getOrgId, orgId));

        Long sequence = count + 1;
        String sequenceStr = String.format("%03d", sequence);

        return examType + year + orgCode + projectDO.getProjectCode() + sequenceStr;
    }

    @Override
    public void update(OrgClassReq req, Long id) {
        // 获取当前登录用户的机构信息
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());

        // 检查是否重名（排除当前记录）
        //        LambdaQueryWrapper<OrgClassDO> wrapper = new LambdaQueryWrapper<>();
        //        wrapper.eq(OrgClassDO::getClassName, req.getClassName())
        //                .eq(OrgClassDO::getOrgId, orgDO.getId())
        //                .ne(OrgClassDO::getId, id);
        //        ValidationUtils.throwIf(baseMapper.selectCount(wrapper) > 0, "班级名称 [ " + req.getClassName() + " ] 已存在");

        // 设置机构 ID 并更新
        req.setOrgId(orgDO.getId());
        super.update(req, id);
    }

    /**
     * 生成二维码内容
     */
    private String buildQrContent(Long classId) throws UnsupportedEncodingException {
        String encryptedClassId = URLEncoder.encode(aesWithHMAC.encryptAndSign(String
                .valueOf(classId)), StandardCharsets.UTF_8);
        return qrcodeUrl + "?classId=" + encryptedClassId;
    }

    /**
     * 生成二维码并上传，返回 URL
     */
    private String generateAndUploadQr(Long candidateId, String qrContent) throws IOException {
        BufferedImage image = QrCodeUtil.generate(qrContent, 300, 300);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            MultipartFile file = new InMemoryMultipartFile("file", candidateId + ".png", "image/png", bytes);

            GeneralFileReq fileReq = new GeneralFileReq();
            fileReq.setType("pic");

            FileInfoResp fileInfo = uploadService.upload(file, fileReq);
            return fileInfo.getUrl();
        }
    }

    /**
     * 根据项目类型和班级类型获取班级选择器
     * orgQueryFlag 1 机构查询 0 后台查询
     *
     * @param projectId
     * @param classType
     * @return
     */
    @Override
    public List<SelectClassVO> getSelectClassByProject(Long projectId, Integer classType, Integer orgQueryFlag) {
        // 查询当前用户属于哪个机构
        Long orgId = null;
        if (SelectClassConstants.ORG_QUERY.equals(orgQueryFlag)) {
            UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
            TedOrgUser tedOrgUser = orgUserMapper.selectOne(new LambdaQueryWrapper<TedOrgUser>()
                    .eq(TedOrgUser::getUserId, userTokenDo.getUserId())
                    .select(TedOrgUser::getOrgId, TedOrgUser::getId)
                    .last("limit 1"));
            orgId = tedOrgUser.getOrgId();
        }
        return baseMapper.getSelectClassByProject(projectId, classType, orgId);
    }

    /**
     * 班级结束报名
     *
     * @param req
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean endApply(OrgClassReq req, Long id) {
        // 先查有没有待审的资料
        Long count = workerApplyMapper.selectCount(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, id)
                .in(WorkerApplyDO::getStatus, Arrays.asList(WorkerApplyReviewStatusEnum.WAIT_UPLOAD
                        .getValue(), WorkerApplyReviewStatusEnum.REJECTED.getValue(), WorkerApplyReviewStatusEnum.DOC_COMPLETE
                        .getValue())));
        ValidationUtils.throwIf(count > 0, "当前班级仍有人员未完成资料上传，暂无法结束报名");
        update(req, id);
        return Boolean.TRUE;
    }

    /**
     * 下载班级缴费通知单
     *
     * @param classId
     * @return
     */
    @Override
    public ResponseEntity<byte[]> downloadPaymentNotice(Long classId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        OrgClassDO orgClass = baseMapper.selectById(classId);
        ValidationUtils.throwIfNull(orgClass, "班级信息不存在");
        String payNoticeUrl = orgClass.getPayNoticeUrl();
        ValidationUtils.throwIf(StringUtil.isBlank(payNoticeUrl), "该班级暂无已审核通过的考生资料");
        byte[] bytes = DownloadOSSFileUtil.downloadUrlToBytes(payNoticeUrl);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * 上传班级缴费凭证
     *
     * @param orgClassPaymentUpdateReq
     * @return
     */
    @Override
    public Boolean uploadProof(OrgClassPaymentUpdateReq orgClassPaymentUpdateReq) {
        Long classId = orgClassPaymentUpdateReq.getId();
        OrgClassDO orgClassDO = baseMapper.selectById(classId);
        ValidationUtils.throwIfNull(orgClassDO, "班级信息不存在");

        // 先判断该班级下是否有考试的报名资料未通过的考生
        Long noPassCount = workerApplyMapper.selectCount(new LambdaQueryWrapper<WorkerApplyDO>()
                .eq(WorkerApplyDO::getClassId, classId)
                .ne(WorkerApplyDO::getStatus, WorkerApplyReviewStatusEnum.APPROVED.getValue()));
        ValidationUtils.throwIf(noPassCount > 0, "班级下存在 " + noPassCount + " 名考生报考资料未通过，请先处理");

        // 统计班级考生人数
        Long personCount = orgClassCandidateMapper.selectCount(new LambdaQueryWrapper<OrgClassCandidateDO>()
                .eq(OrgClassCandidateDO::getClassId, classId));
        ValidationUtils.throwIf(personCount <= 0, "该班级下无考生信息");

        OrgClassDO update = new OrgClassDO();
        update.setId(orgClassDO.getId());
        update.setPayStatus(orgClassPaymentUpdateReq.getPayStatus());
        update.setPayProofUrl(orgClassPaymentUpdateReq.getPayProofUrl());
        update.setPaySubmitTime(LocalDateTime.now());
        update.setRejectReason("");
        baseMapper.updateById(update);
        return Boolean.TRUE;
    }

    /**
     * 审核班级缴费凭证
     *
     * @param reviewPaymentReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reviewUploadProof(ReviewPaymentReq reviewPaymentReq) {
        List<Long> reviewIds = reviewPaymentReq.getReviewIds();
        ValidationUtils.throwIfEmpty(reviewIds, "没有选择任何班级进行缴费审核");

        List<OrgClassDO> orgClassDOS = baseMapper.selectByIds(reviewIds);
        ValidationUtils.throwIfEmpty(orgClassDOS, "未找到需要审核的班级缴费信息");

        Integer auditStatus = reviewPaymentReq.getAuditStatus();

        List<OrgClassDO> updateList = orgClassDOS.stream().map(item -> {
            OrgClassDO update = new OrgClassDO();
            update.setId(item.getId());
            update.setPayStatus(auditStatus);

            if (OrgClassPayStatusEnum.AUDIT_REJECTED.getCode().equals(auditStatus)) {
                update.setRejectReason(reviewPaymentReq.getRejectReason());
            }
            return update;
        }).toList();

        // 批量更新
        baseMapper.updateBatchById(updateList);

        return Boolean.TRUE;
    }

    /**
     * 后台根据作业人员班级查询报名信息
     *
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<OrgClassResp> adminQueryWorkerClassPage(OrgClassQuery query, PageQuery pageQuery) {
        return page(query, pageQuery);
    }

    @Override
    public PageResp<OrgClassResp> adminQueryPayAuditPage(OrgClassQuery query, PageQuery pageQuery) {
        return page(query, pageQuery);
    }

}