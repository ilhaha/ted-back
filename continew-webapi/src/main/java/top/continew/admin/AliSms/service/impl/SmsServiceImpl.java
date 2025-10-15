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

package top.continew.admin.AliSms.service.impl;

import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.AliSms.service.SmsService;
import top.continew.admin.config.ali.AliYunConfig;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final AsyncClient smsAsyncClient;

    private final AliYunConfig smsConfig;

    @Override
    public CompletableFuture<SendSmsResponse> sendSms(String phone, String templateKey, String params) {
        String templateCode = smsConfig.getTemplateCodes().get(templateKey);
        if (templateCode == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("无效的模板key"));
        }

        SendSmsRequest request = SendSmsRequest.builder()
            .phoneNumbers(phone)//手机号
            .signName(smsConfig.getSignName())
            .templateCode(templateCode)
            .templateParam(params)//验证码
            .build();
        return smsAsyncClient.sendSms(request);

    }
}
