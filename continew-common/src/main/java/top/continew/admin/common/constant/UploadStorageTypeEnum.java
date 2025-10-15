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

package top.continew.admin.common.constant;

/**
 * @Author ilhaha
 * @Create 2025/3/24 10:17
 * @Version 1.0
 */
public enum UploadStorageTypeEnum {
    OSS("aliyun_oss", "aliyun"), CERTIFICATE("certificate", "local_certificate"), HEAD("head", "local_head"),
    PIC("pic", "local_pic"), VIDEO("video", "local_video");

    private final String key;
    private final String value;

    // 构造函数
    UploadStorageTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // 获取常量对应的值
    public String getValue() {
        return value;
    }

    // 获取常量的描述信息
    public String getKey() {
        return key; // 修复此处，返回 key 字段的值
    }

    // 根据 key 获取对应的枚举类型
    public static UploadStorageTypeEnum getByKey(String key) {
        for (UploadStorageTypeEnum type : UploadStorageTypeEnum.values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown upload type: " + key);
    }
}