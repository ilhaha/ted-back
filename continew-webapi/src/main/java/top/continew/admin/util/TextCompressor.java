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

package top.continew.admin.util;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.*;

@Component
public class TextCompressor {
    private static final int BUFFER_SIZE = 1024;

    // 压缩并Base64编码
    public static String compress(String text) throws IOException {
        if (text == null || text.isEmpty())
            return "";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 创建GZIPOutputStream (会确保关闭)
            try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                gzip.write(text.getBytes(StandardCharsets.UTF_8));
                // 显式调用finish以确保压缩完成
                gzip.finish();
            } // 此处gzip会自动关闭

            return Base64.getEncoder().encodeToString(out.toByteArray());
        }
    }

    // Base64解码并解压
    public static String decompress(String compressedBase64) throws IOException {
        if (compressedBase64 == null || compressedBase64.isEmpty())
            return "";

        byte[] compressedBytes = Base64.getDecoder().decode(compressedBase64);

        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzip = new GZIPInputStream(bis); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        }
    }
}