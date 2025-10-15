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

package top.continew.admin.document.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.system.model.entity.StorageDO;
import top.continew.admin.system.service.StorageService;
import top.continew.starter.web.model.R;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author ilhaha
 * @Create 2025/3/12 17:11
 * @Version 1.0
 */
@RequestMapping("/document/custom")
@RestController
public class CustomDocumentController {

    @Resource
    private StorageService storageService;

    /**
     * 上传考生资料
     * 
     * @return
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) {
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return R.ok("文件为空");
        }
        StorageDO defaultStorage = storageService.getDefaultStorage();
        // 获取文件扩展名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // 生成唯一的文件名
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // 拼接文件的保存路径
        String realPath = defaultStorage.getBucketName() + fileExtension + "/" + newFilename;

        // 创建目标文件路径
        File realPathFile = new File(realPath);

        // 如果文件目录不存在，则创建
        if (!realPathFile.getParentFile().exists()) {
            realPathFile.getParentFile().mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(realPathFile);

        } catch (IOException e) {
            e.printStackTrace();
            return R.ok("上传失败");
        }

        return R.ok(fileExtension + "/" + newFilename);
    }

}
