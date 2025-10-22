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

package top.continew.admin.controller.upload;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.model.resp.IdCardFileInfoResp;
import top.continew.admin.system.model.resp.file.FileUploadResp;
import top.continew.admin.system.service.UploadService;
import top.continew.starter.core.validation.ValidationUtils;

/**
 * @Author ilhaha
 * @Create 2025/3/24 09:46
 * @Version 1.0
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Resource
    private UploadService uploadService;

    @SaIgnore
    @Operation(summary = "上传文件（用于机构报名考试补充报考资料）", description = "上传文件")
    @PostMapping("/apply/file")
    public FileUploadResp applyUpload(MultipartFile file) {
        ValidationUtils.throwIf(file::isEmpty, "文件不能为空");
        FileInfoResp fileInfo = uploadService.applyUpload(file);
        return FileUploadResp.builder()
                .id(fileInfo.getId())
                .url(fileInfo.getUrl())
                .thUrl(fileInfo.getThUrl())
                .duration(fileInfo.getDuration())
                .metadata(fileInfo.getMetadata())
                .build();
    }


    @Operation(summary = "上传文件", description = "上传文件")
    @PostMapping("/file")
    public FileUploadResp upload(MultipartFile file, GeneralFileReq fileReq) {
        ValidationUtils.throwIf(file::isEmpty, "文件不能为空");
        FileInfoResp fileInfo = uploadService.upload(file, fileReq);
        return FileUploadResp.builder()
                .id(fileInfo.getId())
                .url(fileInfo.getUrl())
                .thUrl(fileInfo.getThUrl())
                .duration(fileInfo.getDuration())
                .metadata(fileInfo.getMetadata())
                .build();
    }

    @SaIgnore
    @Operation(summary = "上传身份证（用于登录实名验证）", description = "（用于登录实名验证）")
    @PostMapping("/file/idCard/{frontOrBack}")
    public IdCardFileInfoResp idCard(MultipartFile file, @PathVariable("frontOrBack") Integer frontOrBack) {
        ValidationUtils.throwIf(file::isEmpty, "文件不能为空");
        return uploadService.uploadIdCard(file, frontOrBack);
    }
}