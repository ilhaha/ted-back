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

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.continew.admin.system.config.oss.AliyunProperties;
import top.continew.admin.system.service.OssImageDownloadService;
import top.continew.admin.util.AliyunTokenUtil;
import top.continew.admin.util.OssClientFactory;

import java.io.File;
import java.time.LocalDate;
import java.util.Properties;

@Slf4j
@Service
public class OssImageDownloadServiceImpl implements OssImageDownloadService {

    private final AliyunProperties aliyunProperties;

    public OssImageDownloadServiceImpl(AliyunProperties aliyunProperties) {
        this.aliyunProperties = aliyunProperties;
    }

    @Override
    public void downloadAllFiles() {

        // 读取 token.properties
        Properties tokenProps = AliyunTokenUtil.load(aliyunProperties.getAccessKeyPath());
        String accessKeyId = AliyunTokenUtil.getKeyId(tokenProps);
        String accessKeySecret = AliyunTokenUtil.getKeySecret(tokenProps);

        //  创建 OSS 客户端
        OSS ossClient = OssClientFactory.build(aliyunProperties.getOss().getEndpoint(), accessKeyId, accessKeySecret);

        // 本地保存根目录
        File baseDir = new File(aliyunProperties.getOss().getLocalBasePath());
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        LocalDate today = LocalDate.now().minusDays(1);//前一天
        String todayPrefix = today.getYear() + "/" + today.getMonthValue() + "/" + today.getDayOfMonth() + "/";

        log.info("开始下载 OSS 文件，prefix={}", todayPrefix);

        String nextMarker = null;
        boolean truncated;

        do {
            ListObjectsRequest request = new ListObjectsRequest();
            request.setBucketName(aliyunProperties.getOss().getBucketName());
            request.setPrefix(todayPrefix);
            request.setMarker(nextMarker);
            request.setMaxKeys(1000);

            ObjectListing listing = ossClient.listObjects(request);

            for (OSSObjectSummary summary : listing.getObjectSummaries()) {
                String key = summary.getKey();
                if (key.endsWith("/")) {
                    continue;
                }

                File localFile = new File(baseDir, key.replace("/", File.separator));
                File parentDir = localFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }

                if (localFile.exists()) {
                    continue;
                }

                ossClient.getObject(new GetObjectRequest(aliyunProperties.getOss().getBucketName(), key), localFile);

                log.info("下载成功：{}", key);
            }

            truncated = listing.isTruncated();
            nextMarker = listing.getNextMarker();

        } while (truncated);

        ossClient.shutdown();
        log.info("OSS 当天文件下载完成，保存路径：{}", baseDir.getAbsolutePath());
    }
}
