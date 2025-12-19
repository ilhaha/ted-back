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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.continew.admin.invigilate.model.req.ExamScoreSubmitReq;
import top.continew.admin.invigilate.model.resp.ExamRespList;
import top.continew.admin.invigilate.service.PlanInvigilateService;

import java.util.List;

/**
 * @author Anton
 * @date 2025/4/24-15:29
 */
@SpringBootTest
@Slf4j
public class PlanInvigilateTest {

//    @Test
//    public void testpageByInvigilatorId(@Autowired PlanInvigilateService planInvigilateService) {
//        ExamRespList examRespList = planInvigilateService.pageByInvigilatorId(547889293968801840L, 1, 10, 1);
//        log.info("result:{}", examRespList);
//    }

    @Test
    void enterGrades(@Autowired PlanInvigilateService planInvigilateService) {
        // 1. 构建请求对象
        ExamScoreSubmitReq req = new ExamScoreSubmitReq();
        req.setExamPlanId(34L);  // 注意：必须用Long类型
        req.setScores(List
            .of(createScoreItem(32L, 33.0, "http://localhost:8000/pic/2025/5/7/681ab10ff11e3e4cfc6b06e2.png"), createScoreItem(321L, 22.0, "http://localhost:8000/pic/2025/5/7/681ab10df11e3e4cfc6b06e1.png")));
        planInvigilateService.enterGrades(req);
    }

    private ExamScoreSubmitReq.ScoreItem createScoreItem(Long studentId, Double score, String url) {
        ExamScoreSubmitReq.ScoreItem item = new ExamScoreSubmitReq.ScoreItem();
        item.setStudentId(studentId);
        item.setScore(score);
        item.setAnswerSheetUrl(url);
        return item;
    }
}
