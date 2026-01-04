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

package top.continew.admin.oss;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.system.service.OssImageDownloadService;

@Slf4j
@SpringBootTest
public class OssDownloadTest {

    @Autowired
    private OssImageDownloadService downloadService;

    @Test
    void testDownloadAllFiles() {
        log.info("==== 开始测试 OSS 全量下载 ====");
        downloadService.downloadAllFiles();
        log.info("==== OSS 全量下载测试结束 ====");
    }
}
