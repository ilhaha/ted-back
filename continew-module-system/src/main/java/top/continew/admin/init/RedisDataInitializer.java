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

package top.continew.admin.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.continew.admin.auth.service.RcDistrictService;

@Slf4j// 日志
@Component// 组件
public class RedisDataInitializer implements CommandLineRunner {

    @Autowired
    private RcDistrictService rcDistrictService;

    @Override
    public void run(String... args) {
        try {
            initDistrictCache();
        } catch (Exception e) {
            throw e;
        }
    }

    private void initDistrictCache() {
        // 所有类
        rcDistrictService.redisInstallLevel1Data();
        // 所有父类
        rcDistrictService.redisInstallAllData();
    }

}
