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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.OrgClassType;
import top.continew.admin.common.model.entity.UserTokenDo;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.exam.mapper.ProjectMapper;
import top.continew.admin.exam.model.entity.ProjectDO;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.service.UploadService;
import top.continew.admin.training.mapper.OrgUserMapper;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.admin.common.util.InMemoryMultipartFile;
import top.continew.starter.core.exception.BusinessException;
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
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    /**
     * 重写分页查询
     *
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

        IPage<OrgClassDetailResp> page;
        if (OrgClassType.INSPECTORS_TYPE.getClassType().equals(query.getClassType())) {
            page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
        } else {
            page = baseMapper.workerClassPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
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

        // 设置机构ID
        req.setOrgId(orgDO.getId());

        // 循环重试生成班级编号并插入
        Long classId = null;
        int retry = 0;
        final int maxRetry = 5;
        while (retry < maxRetry) {
            retry++;

            // 生成班级编号
            String className = generateClassCode(req, orgDO.getCode());
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

        // 如果是工人班级，生成二维码
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
    private String generateClassCode(OrgClassReq req, String orgCode) {
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
        LocalDateTime startOfYear = Year.now().atDay(1).atStartOfDay();
        LocalDateTime endOfYear = Year.now().atMonth(12).atEndOfMonth().atTime(23, 59, 59);

        Long count = baseMapper.selectCount(new LambdaQueryWrapper<OrgClassDO>().eq(OrgClassDO::getClassType, req
            .getClassType()).ge(OrgClassDO::getCreateTime, startOfYear).le(OrgClassDO::getCreateTime, endOfYear));

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
     *
     * @param projectId
     * @param classType
     * @return
     */
    @Override
    public List<SelectClassVO> getSelectClassByProject(Long projectId, Integer classType) {
        return baseMapper.getSelectClassByProject(projectId, classType);
    }
}