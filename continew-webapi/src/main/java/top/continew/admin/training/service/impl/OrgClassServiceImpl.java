package top.continew.admin.training.service.impl;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.OrgClassType;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.util.InMemoryMultipartFile;
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

    /**
     * 重写分页查询
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<OrgClassResp> page(OrgClassQuery query, PageQuery pageQuery) {
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());
        QueryWrapper<OrgClassDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("toc.org_id", orgDO.getId());
        queryWrapper.eq("toc.is_deleted", 0);
        super.sort(queryWrapper, pageQuery);

        IPage<OrgClassDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<OrgClassResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }

    /**
     * 重写添加
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(OrgClassReq req) {
        // 当前用户与所属机构
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());

        // 校验重名
        LambdaQueryWrapper<OrgClassDO> query = new LambdaQueryWrapper<>();
        query.eq(OrgClassDO::getClassName, req.getClassName())
                .eq(OrgClassDO::getOrgId, orgDO.getId());
        ValidationUtils.throwIf(baseMapper.selectCount(query) > 0,
                " [ " + req.getClassName() + " ] 已存在");

        // 设置机构ID
        req.setOrgId(orgDO.getId());

        //  先插入，拿到 classId
        Long classId = super.add(req);
        if (OrgClassType.WORKER_TYPE.getClassType().equals(req.getClassType())) {
            try {
                //  生成二维码
                String qrContent = buildQrContent(classId);
                String qrUrl = generateAndUploadQr(classId, qrContent);

                //  更新二维码地址
                baseMapper.update(
                        new LambdaUpdateWrapper<OrgClassDO>()
                                .eq(OrgClassDO::getId, classId)
                                .set(OrgClassDO::getQrcodeApplyUrl, qrUrl)
                );
            } catch (Exception e) {
                throw new BusinessException("二维码生成失败，请稍后重试");
            }
        }
        return classId;
    }



    @Override
    public void update(OrgClassReq req, Long id) {
        // 获取当前登录用户的机构信息
        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        OrgDO orgDO = orgUserMapper.selectOrgByUserId(userTokenDo.getUserId());

        // 检查是否重名（排除当前记录）
        LambdaQueryWrapper<OrgClassDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgClassDO::getClassName, req.getClassName())
                .eq(OrgClassDO::getOrgId, orgDO.getId())
                .ne(OrgClassDO::getId, id);
        ValidationUtils.throwIf(
                baseMapper.selectCount(wrapper) > 0,
                "班级名称 [ " + req.getClassName() + " ] 已存在"
        );

        // 设置机构 ID 并更新
        req.setOrgId(orgDO.getId());
        super.update(req, id);
    }


    /**
     * 生成二维码内容
     */
    private String buildQrContent(Long classId) throws UnsupportedEncodingException {
        String encryptedClassId = URLEncoder.encode(aesWithHMAC.encryptAndSign(String.valueOf(classId)), StandardCharsets.UTF_8);
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

            MultipartFile file = new InMemoryMultipartFile(
                    "file",
                    candidateId + ".png",
                    "image/png",
                    bytes
            );

            GeneralFileReq fileReq = new GeneralFileReq();
            fileReq.setType("pic");

            FileInfoResp fileInfo = uploadService.upload(file, fileReq);
            return fileInfo.getUrl();
        }
    }
}