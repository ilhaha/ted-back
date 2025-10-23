package top.continew.admin.training.service.impl;

import cn.crane4j.core.util.ObjectUtils;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.OrgClassCandidateMapper;
import top.continew.admin.training.model.entity.OrgClassCandidateDO;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.service.OrgService;
import top.continew.admin.util.InMemoryMultipartFile;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.OrgCandidateMapper;
import top.continew.admin.training.model.entity.OrgCandidateDO;
import top.continew.admin.training.model.query.OrgCandidateQuery;
import top.continew.admin.training.model.req.OrgCandidateReq;
import top.continew.admin.training.model.resp.OrgCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgCandidateResp;
import top.continew.admin.training.service.OrgCandidateService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 机构考生关联业务实现
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Service
@RequiredArgsConstructor
public class OrgCandidateServiceImpl extends BaseServiceImpl<OrgCandidateMapper, OrgCandidateDO, OrgCandidateResp, OrgCandidateDetailResp, OrgCandidateQuery, OrgCandidateReq> implements OrgCandidateService {


    @Resource
    private OrgService orgService;

    @Resource
    private OrgClassCandidateMapper orgClassCandidateMapper;

    @Value("${qrcode.url}")
    private String qrcodeUrl;

    @Resource
    private UploadService uploadService;

    @Resource
    private AESWithHMAC aesWithHMAC;

    /**
     * 重写分页
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<OrgCandidateResp> page(OrgCandidateQuery query, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        Long orgId = orgService.getOrgId(userTokenDo.getUserId());
        QueryWrapper<OrgCandidateDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("toc.org_id", orgId);
        queryWrapper.eq("toc.is_deleted", 0);
        if ("add".equals(query.getType())) {
            queryWrapper.eq("toc.status", 1);
        } else {
            queryWrapper.eq("toc.status", 2);
        }
        if (ObjectUtils.isNotEmpty(query.getCandidateName())) {
            queryWrapper.like("tei.real_name", query.getCandidateName());
        }
        queryWrapper.orderByAsc("toc.create_time");
        super.sort(queryWrapper, pageQuery);
        IPage<OrgCandidateResp> page = baseMapper.getCandidatesList(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        PageResp<OrgCandidateResp> pageResp = PageResp.build(page, OrgCandidateResp.class);
        pageResp.getList().forEach(this::fill);
        return pageResp;
    }


    /**
     * 机构审核考生加入机构
     * @param orgCandidateReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean review(OrgCandidateReq orgCandidateReq) {
        if (orgCandidateReq.getStatus().equals(2)) {
            OrgClassCandidateDO orgClassCandidateDO = new OrgClassCandidateDO();
            orgClassCandidateDO.setCandidateId(orgCandidateReq.getCandidateId());
            orgClassCandidateDO.setClassId(orgCandidateReq.getOrClassId());
            orgClassCandidateMapper.insert(orgClassCandidateDO);
        }

        // 8. 更新考生申请表
        super.update(orgCandidateReq, orgCandidateReq.getId());
        return true;
    }




}