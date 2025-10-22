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

package top.continew.admin.config.ali;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;

import lombok.Data;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.io.*;

/**
 * 类说明:
 *
 * @author 丶Anton
 * @email itanton666@gmail.com
 * @date 2025/3/24 13:48
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun")
@Data
public class AliYunConfig {

    private String accessKeyPath;

    private String keyId;

    private String keySecret;

    private String region;

    private String endpoint;

    private String signName;

    private Map<String, String> templateCodes;

    @Bean
    public AsyncClient asyncClient() {
        try {
            // 读取本地文件
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream(accessKeyPath);
            properties.load(fis);
            String accessKeyId = properties.getProperty(keyId);
            String accessKeySecret = properties.getProperty(keySecret);

            // 创建认证提供者
            StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

            // 初始化 AsyncClient
            return AsyncClient.builder()
                .region(region) // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(ClientOverrideConfiguration.create().setEndpointOverride(endpoint)

                )
                .build();
        } catch (Exception e) {
            throw new RuntimeException("初始化 异步AsyncClient 失败", e);
        }
    }

}
