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

package top.continew.admin;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.starter.core.autoconfigure.project.ProjectProperties;
import top.continew.starter.extension.crud.annotation.EnableCrudRestController;
import top.continew.starter.web.annotation.EnableGlobalResponse;
import top.continew.starter.web.model.R;

/**
 * 启动程序
 *
 * @author Charles7c
 * @since 2022/12/8 23:15
 */
@Slf4j
@EnableFileStorage
@EnableMethodCache(basePackages = "top.continew.admin")
@EnableGlobalResponse
@EnableCrudRestController
@RestController
@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
public class ContiNewAdminApplication {

    private final ProjectProperties projectProperties;

    public static void main(String[] args) {
        SpringApplication.run(ContiNewAdminApplication.class, args);
    }

    @Hidden
    @SaIgnore
    @GetMapping("/")
    public R index() {
        return R.ok(projectProperties);
    }
}
