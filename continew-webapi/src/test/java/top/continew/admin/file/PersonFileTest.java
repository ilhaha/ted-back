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

package top.continew.admin.file;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.file.model.entity.PersonFile;
import top.continew.admin.file.model.vo.PersonFileVo;
import top.continew.admin.file.service.PersonFileService;

@SpringBootTest
@Slf4j
public class PersonFileTest {

    @Test()
    public void testPersonFile(@Autowired PersonFileService personFileService) {
        Page<PersonFile> personFilePage = new Page<>();
        personFilePage.setCurrent(1);
        personFilePage.setSize(10);
        PersonFileVo personFiles = personFileService.queryPersonFile(personFilePage);
        log.info("考生资料:{}", personFiles);
    }
}
