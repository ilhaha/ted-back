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

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class AESWithHMAC {

    private static final Logger logger = Logger.getLogger(AESWithHMAC.class.getName());

    @Value("${aes.key}")
    private String aesKeyStr;

    @Value("${aes.hmac-key}")
    private String hmacKeyStr;

    @Value("${aes.iv}")
    private String ivStr;

    @Value("${aes.cipher}")
    private String cipherMode;

    @Value("${aes.algorithm}")
    private String algorithm;

    @Value("${aes.hmac-sign}")
    private String hmacAlgorithm;

    private SecretKeySpec aesKeySpec;
    private SecretKeySpec hmacKeySpec;
    private IvParameterSpec ivSpec;

    @PostConstruct
    public void init() {
        try {
            aesKeySpec = new SecretKeySpec(aesKeyStr.getBytes(StandardCharsets.UTF_8), algorithm);
            hmacKeySpec = new SecretKeySpec(hmacKeyStr.getBytes(StandardCharsets.UTF_8), hmacAlgorithm);
            ivSpec = new IvParameterSpec(ivStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "初始化密钥失败", e);
        }
    }

    /**
     * 加密并签名
     */
    public String encryptAndSign(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

            Mac mac = Mac.getInstance(hmacAlgorithm);
            mac.init(hmacKeySpec);
            byte[] hmacBytes = mac.doFinal(encryptedBase64.getBytes(StandardCharsets.UTF_8));
            String hmacBase64 = Base64.getEncoder().encodeToString(hmacBytes);

            return encryptedBase64 + ":" + hmacBase64;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "加密或签名失败", e);
            return null;
        }
    }

    /**
     * 验签并解密
     */
    public String verifyAndDecrypt(String payload) {
        try {
            String[] parts = payload.split(":");
            if (parts.length != 2) {
                logger.warning("非法数据格式");
                return null;
            }

            String encryptedBase64 = parts[0];
            String receivedHmacBase64 = parts[1];

            Mac mac = Mac.getInstance(hmacAlgorithm);
            mac.init(hmacKeySpec);
            byte[] computedHmac = mac.doFinal(encryptedBase64.getBytes(StandardCharsets.UTF_8));
            String computedHmacBase64 = Base64.getEncoder().encodeToString(computedHmac);

            if (!computedHmacBase64.equals(receivedHmacBase64)) {
                logger.warning("HMAC 验证失败，数据可能被篡改");
                return null;
            }

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "解密或验签失败", e);
            return null;
        }
    }
}