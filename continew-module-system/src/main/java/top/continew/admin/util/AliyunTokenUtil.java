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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 阿里云 Token 工具类
 * <p>
 * 统一从 token.properties 读取 AccessKeyId 和 AccessKeySecret
 */
public class AliyunTokenUtil {

    private AliyunTokenUtil() {
    }

    /**
     * 加载 token.properties 文件
     *
     * @param path 文件路径，例如 E:\ted\token.properties
     * @return Properties 对象
     */
    public static Properties load(String path) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("读取阿里云 token.properties 失败: " + path, e);
        }
        return properties;
    }

    /**
     * 获取 AccessKeyId
     *
     * @param properties Properties 对象
     * @return AccessKeyId
     */
    public static String getKeyId(Properties properties) {
        String keyId = properties.getProperty("ALIBABA_CLOUD_ACCESS_KEY_ID");
        if (keyId == null || keyId.isBlank()) {
            throw new RuntimeException("token.properties 中缺少 ALIBABA_CLOUD_ACCESS_KEY_ID");
        }
        return keyId;
    }

    /**
     * 获取 AccessKeySecret
     *
     * @param properties Properties 对象
     * @return AccessKeySecret
     */
    public static String getKeySecret(Properties properties) {
        String keySecret = properties.getProperty("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
        if (keySecret == null || keySecret.isBlank()) {
            throw new RuntimeException("token.properties 中缺少 ALIBABA_CLOUD_ACCESS_KEY_SECRET");
        }
        return keySecret;
    }
}
