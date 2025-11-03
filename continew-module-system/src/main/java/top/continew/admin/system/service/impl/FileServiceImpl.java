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

package top.continew.admin.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.ProgressListener;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.constant.UploadStorageTypeEnum;
import top.continew.admin.common.constant.UploadTypeConstants;
import top.continew.admin.common.model.entity.IdCardDo;
import top.continew.admin.common.service.ali.ocr.IdCardRecognition;
import top.continew.admin.common.util.VideoUtil;
import top.continew.admin.system.enums.FileTypeEnum;
import top.continew.admin.system.mapper.FileMapper;
import top.continew.admin.system.model.entity.FileDO;
import top.continew.admin.system.model.entity.StorageDO;
import top.continew.admin.system.model.query.FileQuery;
import top.continew.admin.system.model.req.FileReq;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.model.resp.IdCardFileInfoResp;
import top.continew.admin.system.model.resp.file.FileResp;
import top.continew.admin.system.model.resp.file.FileStatisticsResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.system.service.StorageService;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.util.StrUtils;
import top.continew.starter.core.util.URLUtils;
import top.continew.starter.core.validation.CheckUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件业务实现
 *
 * @author Charles7c
 * @since 2023/12/23 10:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends BaseServiceImpl<FileMapper, FileDO, FileResp, FileResp, FileQuery, FileReq> implements FileService {

    private final FileStorageService fileStorageService;
    @Resource
    private StorageService storageService;

    @Resource
    private IdCardRecognition idCardRecognition;

    @Override
    protected void beforeDelete(List<Long> ids) {
        List<FileDO> fileList = baseMapper.lambdaQuery().in(FileDO::getId, ids).list();
        Map<Long, List<FileDO>> fileListGroup = fileList.stream().collect(Collectors.groupingBy(FileDO::getStorageId));
        for (Map.Entry<Long, List<FileDO>> entry : fileListGroup.entrySet()) {
            StorageDO storage = storageService.getById(entry.getKey());
            for (FileDO file : entry.getValue()) {
                FileInfo fileInfo = file.toFileInfo(storage);
                fileStorageService.delete(fileInfo);
            }
        }
    }

    @Override
    public FileInfo upload(MultipartFile file, String storageCode) {
        StorageDO storage;
        if (StrUtil.isBlank(storageCode)) {
            storage = storageService.getDefaultStorage();
            CheckUtils.throwIfNull(storage, "请先指定默认存储");
        } else {
            storage = storageService.getByCode(storageCode);
            CheckUtils.throwIfNotExists(storage, "StorageDO", "Code", storageCode);
        }
        LocalDate today = LocalDate.now();
        String path = today.getYear() + StringConstants.SLASH + today.getMonthValue() + StringConstants.SLASH + today
            .getDayOfMonth() + StringConstants.SLASH;
        UploadPretreatment uploadPretreatment = fileStorageService.of(file)
            .setPlatform(storage.getCode())
            .setHashCalculatorMd5(true)
            .putAttr(ClassUtil.getClassName(StorageDO.class, false), storage)
            .setPath(path);
        // 图片文件生成缩略图
        if (FileTypeEnum.IMAGE.getExtensions().contains(FileNameUtil.extName(file.getOriginalFilename()))) {
            uploadPretreatment.thumbnail(img -> img.size(100, 100));
        }
        uploadPretreatment.setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                log.info("开始上传");
            }

            @Override
            public void progress(long progressSize, Long allSize) {
                log.info("已上传 [{}]，总大小 [{}]", progressSize, allSize);
            }

            @Override
            public void finish() {
                log.info("上传结束");
            }
        });
        // 处理本地存储文件 URL
        FileInfo fileInfo = uploadPretreatment.upload();
        String domain = StrUtil.appendIfMissing(storage.getDomain(), StringConstants.SLASH);
        fileInfo.setUrl(URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getFilename()));
        fileInfo.setThUrl(URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getThFilename()));
        return fileInfo;
    }

    @Override
    public Long countByStorageIds(List<Long> storageIds) {
        if (CollUtil.isEmpty(storageIds)) {
            return 0L;
        }
        return baseMapper.lambdaQuery().in(FileDO::getStorageId, storageIds).count();
    }

    @Override
    public FileStatisticsResp statistics() {
        FileStatisticsResp resp = new FileStatisticsResp();
        List<FileStatisticsResp> statisticsList = baseMapper.statistics();
        if (CollUtil.isEmpty(statisticsList)) {
            return resp;
        }
        resp.setData(statisticsList);
        resp.setSize(statisticsList.stream().mapToLong(FileStatisticsResp::getSize).sum());
        resp.setNumber(statisticsList.stream().mapToLong(FileStatisticsResp::getNumber).sum());
        return resp;
    }

    @Override
    public FileInfoResp upload(MultipartFile file, GeneralFileReq fileReq) {
        String storageCode = UploadStorageTypeEnum.OSS.getValue();
        StorageDO storage;
        if (StrUtil.isBlank(storageCode)) {
            storage = storageService.getDefaultStorage();
            CheckUtils.throwIfNull(storage, "请先指定默认存储");
        } else {
            storage = storageService.getByCode(storageCode);
            CheckUtils.throwIfNotExists(storage, "StorageDO", "Code", storageCode);
        }
        LocalDate today = LocalDate.now();
        String path = today.getYear() + StringConstants.SLASH + today.getMonthValue() + StringConstants.SLASH + today
            .getDayOfMonth() + StringConstants.SLASH;

        UploadPretreatment uploadPretreatment = fileStorageService.of(file)
            .setPlatform(storage.getCode())
            .setHashCalculatorMd5(true)
            .putAttr(ClassUtil.getClassName(StorageDO.class, false), storage)
            .setPath(path);

        // 图片文件生成缩略图
        if (FileTypeEnum.IMAGE.getExtensions().contains(FileNameUtil.extName(file.getOriginalFilename()))) {
            uploadPretreatment.thumbnail(img -> img.size(100, 100));
        }
        uploadPretreatment.setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                log.info("开始上传");
            }

            @Override
            public void progress(long progressSize, Long allSize) {
                log.info("已上传 [{}]，总大小 [{}]", progressSize, allSize);
            }

            @Override
            public void finish() {
                log.info("上传结束");
            }
        });
        // 处理本地存储文件 URL
        FileInfo fileInfo = uploadPretreatment.upload();
        String domain = StrUtil.appendIfMissing(storage.getDomain(), StringConstants.SLASH);
        fileInfo.setUrl(URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getFilename()));
        fileInfo.setThUrl(UploadTypeConstants.PIC.equals(fileReq.getType())
            ? URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getThFilename())
            : null);
        FileInfoResp fileInfoResp = new FileInfoResp();
        BeanUtils.copyProperties(fileInfo, fileInfoResp);
        if (UploadTypeConstants.VIDEO.equals(fileReq.getType())) {
            long result = VideoUtil.getDuration(storage.getDomain() + "/" + path + fileInfo.getFilename());
            fileInfoResp.setDuration(result);
        } else if (UploadTypeConstants.CERTIFICATE.equals(fileReq.getType())) {
            IdCardDo idCardDo = null;
            try {
                idCardDo = idCardRecognition.uploadIdCard(file.getInputStream(), fileReq.getIdCardFrontOrBack());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ValidationUtils.throwIf(ObjectUtils.isEmpty(idCardDo), "身份证信息识别失败");
        }
        return fileInfoResp;
    }
    /**
     * 上传身份证或人脸证件照（用于登录实名验证）
     * @param file 文件
     * @param frontOrBack 1=身份证正面，0=身份证反面，2=人脸证件照
     */
    @Override
    public IdCardFileInfoResp uploadIdCard(MultipartFile file, Integer frontOrBack) {
        String storageCode = UploadStorageTypeEnum.OSS.getValue();
        StorageDO storage;
        if (StrUtil.isBlank(storageCode)) {
            storage = storageService.getDefaultStorage();
            CheckUtils.throwIfNull(storage, "请先指定默认存储");
        } else {
            storage = storageService.getByCode(storageCode);
            CheckUtils.throwIfNotExists(storage, "StorageDO", "Code", storageCode);
        }

        LocalDate today = LocalDate.now();
        String path = today.getYear() + StringConstants.SLASH + today.getMonthValue()
                + StringConstants.SLASH + today.getDayOfMonth() + StringConstants.SLASH;

        UploadPretreatment uploadPretreatment = fileStorageService.of(file)
                .setPlatform(storage.getCode())
                .setHashCalculatorMd5(true)
                .putAttr(ClassUtil.getClassName(StorageDO.class, false), storage)
                .setPath(path);

        // 图片文件生成缩略图
        if (FileTypeEnum.IMAGE.getExtensions().contains(FileNameUtil.extName(file.getOriginalFilename()))) {
            uploadPretreatment.thumbnail(img -> img.size(100, 100));
        }
        uploadPretreatment.setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                log.info("开始上传");
            }
            @Override
            public void progress(long progressSize, Long allSize) {
                log.info("已上传 [{}]，总大小 [{}]", progressSize, allSize);
            }
            @Override
            public void finish() {
                log.info("上传结束");
            }
        });

        // 上传文件
        FileInfo fileInfo = uploadPretreatment.upload();
        String domain = StrUtil.appendIfMissing(storage.getDomain(), StringConstants.SLASH);
        fileInfo.setUrl(URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getFilename()));
        IdCardFileInfoResp fileInfoResp = new IdCardFileInfoResp();
        BeanUtils.copyProperties(fileInfo, fileInfoResp);

        // 处理人脸证件照
        if (frontOrBack != null && frontOrBack == 2) {
            try {
                BufferedImage img = ImageIO.read(file.getInputStream());
                int width = img.getWidth();
                int height = img.getHeight();

                // 校验尺寸约为 295x413
                if (Math.abs(width - 295) > 10 || Math.abs(height - 413) > 10) {
                    throw new BusinessException("一寸免冠照尺寸不符合要求，应为约 295×413 像素");
                }

                // 校验文件大小
                if (file.getSize() > 2 * 1024 * 1024) {
                    throw new BusinessException("人脸证件照大小不能超过 2MB");
                }

                fileInfoResp.setFacePhoto(fileInfo.getUrl());
                log.info("人脸证件照上传成功：{}", fileInfo.getUrl());
                return fileInfoResp;

            } catch (Exception e) {
                log.error("人脸证件照上传失败：{}", e.getMessage(), e);
                throw new BusinessException(e.getMessage());
            }
        }
        // 身份证识别逻辑
        try {
            boolean flag = frontOrBack == 1;
            IdCardDo idCardDo = idCardRecognition.uploadIdCard(file.getInputStream(), flag);
            ValidationUtils.throwIf(ObjectUtils.isEmpty(idCardDo), "身份证信息识别失败");
            if (flag) {
                fileInfoResp.setRealName(idCardDo.getName());
                fileInfoResp.setGender(idCardDo.getSex() ? "女" : "男");
                fileInfoResp.setNation(idCardDo.getEthnicity());
                fileInfoResp.setBirthDate(idCardDo.getBirthDate());
                fileInfoResp.setAddress(idCardDo.getAddress());
                fileInfoResp.setIdCardNumber(idCardDo.getIdNumber());
            } else {
                fileInfoResp.setIssuingAuthority(idCardDo.getIssueAuthority());
                fileInfoResp.setValidStartDate(idCardDo.getSetValidPeriodStart());
                fileInfoResp.setValidEndDate(idCardDo.getSetValidPeriodEnd());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileInfoResp;
    }

    /**
     * 上传文件（用于机构报名考试补充报考资料）
     * @param file
     * @return
     */
    @Override
    public FileInfoResp applyUpload(MultipartFile file) {
        String storageCode = UploadStorageTypeEnum.OSS.getValue();
        StorageDO storage;
        if (StrUtil.isBlank(storageCode)) {
            storage = storageService.getDefaultStorage();
            CheckUtils.throwIfNull(storage, "请先指定默认存储");
        } else {
            storage = storageService.getByCode(storageCode);
            CheckUtils.throwIfNotExists(storage, "StorageDO", "Code", storageCode);
        }
        LocalDate today = LocalDate.now();
        String path = today.getYear() + StringConstants.SLASH + today.getMonthValue() + StringConstants.SLASH + today
                .getDayOfMonth() + StringConstants.SLASH;

        UploadPretreatment uploadPretreatment = fileStorageService.of(file)
                .setPlatform(storage.getCode())
                .setHashCalculatorMd5(true)
                .putAttr(ClassUtil.getClassName(StorageDO.class, false), storage)
                .setPath(path);

        // 图片文件生成缩略图
        if (FileTypeEnum.IMAGE.getExtensions().contains(FileNameUtil.extName(file.getOriginalFilename()))) {
            uploadPretreatment.thumbnail(img -> img.size(100, 100));
        }
        uploadPretreatment.setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                log.info("开始上传");
            }

            @Override
            public void progress(long progressSize, Long allSize) {
                log.info("已上传 [{}]，总大小 [{}]", progressSize, allSize);
            }

            @Override
            public void finish() {
                log.info("上传结束");
            }
        });
        // 处理本地存储文件 URL
        FileInfo fileInfo = uploadPretreatment.upload();
        String domain = StrUtil.appendIfMissing(storage.getDomain(), StringConstants.SLASH);
        fileInfo.setUrl(URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getFilename()));
        fileInfo.setThUrl(URLUtil.normalize(domain + fileInfo.getPath() + fileInfo.getThFilename()));
        FileInfoResp fileInfoResp = new FileInfoResp();
        BeanUtils.copyProperties(fileInfo, fileInfoResp);
        return fileInfoResp;
    }

    @Override
    protected void fill(Object obj) {
        super.fill(obj);
        if (obj instanceof FileResp fileResp && !URLUtils.isHttpUrl(fileResp.getUrl())) {
            StorageDO storage = storageService.getById(fileResp.getStorageId());
            String prefix = StrUtil.appendIfMissing(storage.getDomain(), StringConstants.SLASH);
            String url = URLUtil.normalize(prefix + fileResp.getUrl());
            fileResp.setUrl(url);
            String thumbnailUrl = StrUtils.blankToDefault(fileResp.getThumbnailUrl(), url, thUrl -> URLUtil
                .normalize(prefix + thUrl));
            fileResp.setThumbnailUrl(thumbnailUrl);
            fileResp.setStorageName("%s (%s)".formatted(storage.getName(), storage.getCode()));
        }
    }
}