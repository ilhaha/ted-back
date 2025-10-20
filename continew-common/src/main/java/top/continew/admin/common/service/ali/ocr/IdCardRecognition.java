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

package top.continew.admin.common.service.ali.ocr;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.ocr_api20210707.models.*;
import com.aliyun.sdk.service.ocr_api20210707.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import darabonba.core.client.ClientOverrideConfiguration;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.continew.admin.common.model.entity.IdCardDo;
import top.continew.admin.common.util.DateUtil;

// import javax.net.ssl.KeyManager;
// import javax.net.ssl.X509TrustManager;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.io.*;

/**
 * 类说明:
 *
 * @author 丶Anton
 * @email itanton666@gmail.com
 * @date 2025/3/24 10:13
 */
@Component
public class IdCardRecognition {

    private AsyncClient client;

    @Value("${aliyun.accessKeyPath}")
    private String ACCESS_KEY_PATH;

    @Value("${aliyun.keyId}")
    private String KEY_ID;

    @Value("${aliyun.keySecret}")
    private String KEY_SECRET;

    @Value("${aliyun.region}")
    private String REGION;

    @Value("${aliyun.ocr.endpoint}")
    private String ENDPOINT;

    private String ACCESS_KEY;
    private String SECRET_ACCESS_KEY;

    private final String EMPTY_CHAR = "";

    private void createAliClient() {
        try {
            if (ACCESS_KEY == null || SECRET_ACCESS_KEY == null) {
                Properties properties = new Properties();
                assert ACCESS_KEY_PATH != null;
                FileInputStream fis = new FileInputStream(ACCESS_KEY_PATH);
                properties.load(fis);
                ACCESS_KEY = properties.getProperty(KEY_ID);
                SECRET_ACCESS_KEY = properties.getProperty(KEY_SECRET);
                fis.close();
            }

            StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                    .accessKeyId(ACCESS_KEY)
                    .accessKeySecret(SECRET_ACCESS_KEY)
                    .build());

            this.client = AsyncClient.builder()
                    .region(REGION)
                    .credentialsProvider(provider)
                    .overrideConfiguration(ClientOverrideConfiguration.create()
                            .setEndpointOverride(ENDPOINT))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public IdCardDo uploadIdCard(InputStream is, boolean flag) throws Exception {
        return uploadIdCard(is, flag, null);
    }

    /**
     *
     * @param is   图片流
     * @param flag 1正0反
     */
    public IdCardDo uploadIdCard(InputStream is, boolean flag, IdCardDo idCardDo) throws Exception {
        // 创建连接对象
        createAliClient();
        RecognizeIdcardResponse resp = null;
        try {
            RecognizeIdcardRequest recognizeIdcardRequest = RecognizeIdcardRequest.builder()
                .url(EMPTY_CHAR)
                .body(is)
                .build();

            CompletableFuture<RecognizeIdcardResponse> response = client.recognizeIdcard(recognizeIdcardRequest);
            resp = response.get();
            new Gson().toJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
            assert is != null;
            is.close();
        }
        return getIdCardDo(resp, flag, idCardDo);
    }

    /**
     * 上传身份证返回
     *
     * @param resp false反面
     * @param flag 正反面 true正面
     * @return
     */
    private @Nullable IdCardDo getIdCardDo(RecognizeIdcardResponse resp, boolean flag, IdCardDo idCardDo) {
        // 获取所需字段
        try {
            String bodyJson = new Gson().toJson(resp.getBody());

            JsonObject bodyObject = new Gson().fromJson(bodyJson, JsonObject.class);
            String data = bodyObject.get("data").getAsString();

            Gson gson = new Gson();
            JsonObject dataObject = gson.fromJson(data, JsonObject.class);
            idCardDo = idCardDo == null ? new IdCardDo() : idCardDo;
            if (flag) {
                //  face.data
                JsonObject faceData = dataObject.getAsJsonObject("data")
                    .getAsJsonObject("face")
                    .getAsJsonObject("data");

                String name = faceData.get("name").getAsString();
                String sex = faceData.get("sex").getAsString();
                String ethnicity = faceData.get("ethnicity").getAsString();
                String birthDate = faceData.get("birthDate").getAsString();
                String address = faceData.get("address").getAsString();
                String idNumber = faceData.get("idNumber").getAsString();

                LocalDate localDate = DateUtil.getLocalDate(birthDate);

                idCardDo.setName(name);
                idCardDo.setSex("女".equals(sex));
                idCardDo.setEthnicity(ethnicity);
                idCardDo.setBirthDate(localDate);
                idCardDo.setAddress(address);
                idCardDo.setIdNumber(idNumber);
            } else {
                // 处理背面数据
                JsonObject backData = dataObject.getAsJsonObject("data")
                    .getAsJsonObject("back")
                    .getAsJsonObject("data");

                // 提取签发机关和有效期
                String issueAuthority = backData.get("issueAuthority").getAsString();
                String validPeriod = backData.get("validPeriod").getAsString();

                idCardDo.setIssueAuthority(issueAuthority);
                String[] split = validPeriod.split("-");
                idCardDo.setSetValidPeriodStart(DateUtil.getLocalDate(split[0], "yyyy.MM.dd"));
                idCardDo.setSetValidPeriodEnd(DateUtil.getLocalDate(split[1], "yyyy.MM.dd"));
            }
            return idCardDo;
        } catch (Exception e) {
            return null;
        }
    }

}
