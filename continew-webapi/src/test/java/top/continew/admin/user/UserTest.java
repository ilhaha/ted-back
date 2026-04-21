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

package top.continew.admin.user;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.service.CandidateTicketService;
import top.continew.admin.system.mapper.UserMapper;
import top.continew.admin.worker.mapper.WorkerExamTicketMapper;

@SpringBootTest
public class UserTest {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @Resource
    private CandidateTicketService candidateTicketReactiveService;
    @Resource
    private WorkerExamTicketMapper workerExamTicketMapper;

    @Test
    public void test() {
        //        System.out.println(aesWithHMAC.encryptAndSign(("11011119910630571X")));
        // jWjEcIeCpsaXz9k1fuFerEUnk3I69wFmeo2LwVqy0uc=:1F7mUAXdAaGGUm2RbADRrMYwzEqUOUhtwyxPp15YLBs=
        //        System.out.println(aesWithHMAC
        //            .verifyAndDecrypt(("VP6PQdUG2HAZsHXhpXqIoHgWiKkou3cxms9VmFInV6y/Sv57poieZ0+WQnvp4eA/:rSs7+WVzb8zurTTUa2Ee9neGbEZ6MqZfysvuks4a0EM=")));
        System.out.println(aesWithHMAC
            .verifyAndDecrypt("VP6PQdUG2HAZsHXhpXqIoDMZ6Y3Dn0PHKNIZBVLPkCkLahpzYn2XdVfNbPI/RVKR:R0cl6KlAv/9uFMnduVH8EzOuqRrwBmDca9Mf2bkU7uM="));
        System.out.println(aesWithHMAC
            .verifyAndDecrypt("nhz9I5VNZ29rroyA8qQcGg==:RpZ9H+Iwa4JHnPtBpmJzaQNUsWAR8AZWVY4enlOg5G0="));
        System.out.println(aesWithHMAC
            .verifyAndDecrypt("IkNSSFm07NxDNkIzUpw8Tg==:SHBqw4l8zlqWi7k2QMeqG9mgX8tXhXEoyGP04fJcSU8="));
        System.out.println(aesWithHMAC
            .verifyAndDecrypt("Rtxqv2RIrkD5e9YUjiye/Q==:Hc3Mm7r+3/unpCThIE4OCtQMFP69IF0YNUl9UVF9zq4="));
        //        System.out.println(aesWithHMAC.encryptAndSign("Q212604210001"));
    }

}
