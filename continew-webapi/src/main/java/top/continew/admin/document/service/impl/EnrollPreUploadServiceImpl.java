package top.continew.admin.document.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.document.mapper.DocumentPreMapper;
import top.continew.admin.document.model.dto.DocFileDTO;
import top.continew.admin.document.model.entity.DocumentPreDO;
import top.continew.admin.document.model.req.QrcodeUploadReq;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
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

import java.util.ArrayList;
import java.util.List;

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
                .eq(OrgClassCandidateDO::getStatus,0);
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
     * 重写分页查询
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