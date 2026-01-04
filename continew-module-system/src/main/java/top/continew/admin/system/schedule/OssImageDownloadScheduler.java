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

package top.continew.admin.system.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.continew.admin.system.service.OssImageDownloadService;

@Slf4j
@Component
public class OssImageDownloadScheduler {

    private final OssImageDownloadService downloadService;

    public OssImageDownloadScheduler(OssImageDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    /** 每天凌晨 1 点执行下载整个 Bucket */
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        log.info("OSS 全量下载任务开始");
        downloadService.downloadAllFiles();
    }
}
