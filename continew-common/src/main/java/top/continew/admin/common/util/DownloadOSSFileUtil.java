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

package top.continew.admin.common.util;

import jodd.util.StringUtil;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.ValidationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author ilhaha
 * @Create 2026/1/9 12:02
 *
 *         下载阿里云OSS文件
 */
public class DownloadOSSFileUtil {

    public static byte[] downloadUrlToBytes(String fileUrl) {
        ValidationUtils.throwIf(StringUtil.isBlank(fileUrl), "文件地址不能为空");
        try {
            URL url = new URL(fileUrl);
            try (InputStream inputStream = url.openStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new BusinessException("系统繁忙", e);
        }
    }

}
