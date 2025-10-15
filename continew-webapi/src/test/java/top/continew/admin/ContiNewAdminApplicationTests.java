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

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.common.model.entity.IdCardDo;
import top.continew.admin.common.service.ali.ocr.IdCardRecognition;

import java.io.*;

@SpringBootTest(classes = ContiNewAdminApplication.class)
class ContiNewAdminApplicationTests {

    @Resource
    private IdCardRecognition idCardRecognition;

    /**
     * 身份证正面识别
     * 
     * @throws Exception
     */
    @Test
    void contextLoads() throws Exception {
        File file = new File("/Users/antonluo/Desktop/idcart.jpg");
        InputStream inputStream = new FileInputStream(file);
        IdCardDo idCardDo = idCardRecognition.uploadIdCard(inputStream, true, null);
        System.out.println(idCardDo);
        File file2 = new File("/Users/antonluo/Desktop/idcart2.png");
        InputStream inputStream2 = new FileInputStream(file2);
        IdCardDo idCardDo2 = idCardRecognition.uploadIdCard(inputStream2, true, null);
        System.out.println(idCardDo2);
    }

    @Test
    void contextLoadsReverse() throws Exception {
        File file = new File("/Users/antonluo/Desktop/idcart.jpg");
        InputStream inputStream = new FileInputStream(file);
        IdCardDo idCardDo = idCardRecognition.uploadIdCard(inputStream, true);
        File file1 = new File("/Users/antonluo/Desktop/bei1.jpg");
        InputStream inputStream1 = new FileInputStream(file1);
        idCardRecognition.uploadIdCard(inputStream1, false, idCardDo);
        System.out.println(idCardDo);
    }

}
