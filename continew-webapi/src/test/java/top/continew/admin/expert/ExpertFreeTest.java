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

package top.continew.admin.expert;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.expert.mapper.ExpertFreeMapper;
import top.continew.admin.expert.model.ExpertFreeResp;

import java.util.List;

/**
 * @author Anton
 * @date 2025/4/27-14:02
 */
@SpringBootTest
@Slf4j
public class ExpertFreeTest {

    @Test
    public void testGetExpertFree(@Autowired ExpertFreeMapper expertFreeMapper) {
        //        UserTokenDo userTokenDo = TokenLocalThreadUtil.get();
        //        Long userId = userTokenDo.getUserId();
        //        Long organizetionId = expertFreeMapper.queryOrganizetionId(userId);
        List<ExpertFreeResp> expertFreeResp = expertFreeMapper.queryExpertFreeByProjectIdAndExpertId(1L, 10, 0);
        Long total = expertFreeMapper.queryExpertFreeByProjectIdAndExpertIdTotal(1L);
        log.info("res:{},total:{}", expertFreeResp, total);
    }

}
