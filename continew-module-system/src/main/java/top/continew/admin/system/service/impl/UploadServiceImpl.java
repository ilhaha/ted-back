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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.system.model.req.file.GeneralFileReq;
import top.continew.admin.system.model.resp.FileInfoResp;
import top.continew.admin.system.model.resp.IdCardFileInfoResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.system.service.UploadService;

/**
 * @Author ilhaha
 * @Create 2025/3/24 10:06
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final FileService fileService;

    /**
     * 上传文件
     *
     * @return 上传后的文件访问路径
     */
    @Override
    public FileInfoResp upload(MultipartFile file, GeneralFileReq fileReq) {
        return fileService.upload(file, fileReq);
    }

    /**
     * 上传身份证
     * @param file
     * @param frontOrBack
     * @return
     */
    @Override
    public IdCardFileInfoResp uploadIdCard(MultipartFile file, Integer frontOrBack) {
        return fileService.uploadIdCard(file, frontOrBack);
    }

}
