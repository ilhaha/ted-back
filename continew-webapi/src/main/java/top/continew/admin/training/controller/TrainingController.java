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

package top.continew.admin.training.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.training.mapper.TrainingMapper;
import top.continew.admin.training.model.entity.TrainingDO;
import top.continew.admin.training.model.req.StudyTimeRecordReq;
import top.continew.admin.training.model.req.TrainTreeReq;
import top.continew.admin.training.model.resp.*;
import top.continew.admin.training.model.vo.ExpertVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.TrainingQuery;
import top.continew.admin.training.model.req.TrainingReq;
import top.continew.admin.training.service.TrainingService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 培训主表管理 API
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Tag(name = "培训主表管理 API")
@RestController
@CrudRequestMapping(value = "/training/training", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
@RequiredArgsConstructor
public class TrainingController extends BaseController<TrainingService, TrainingResp, TrainingDetailResp, TrainingQuery, TrainingReq> {

    private final TrainingService trainingService;

    private final TrainingMapper trainingMapper;

    @PostMapping("/getTree")
    public TrainTreeResp getTree(@RequestBody TrainTreeReq trainTreeReq) {
        return trainingService.getTree(trainTreeReq);
    }

    @PostMapping("/getStuTree")
    public TrainTreeResp getStuTree(@RequestBody TrainTreeReq trainTreeReq) {
        return trainingService.getStuTree(trainTreeReq);
    }

    @GetMapping("/getAllTraining")
    public PageResp<TrainingResp> getAllTraining(@Validated TrainingQuery query, @Validated PageQuery pageQuery) {
        return trainingService.getAllTraining(query, pageQuery);
    }

    //更新学习时长
    @PostMapping("/updateStudyTimeRecord")
    public Boolean updateStudyTimeRecord(@Validated @RequestBody StudyTimeRecordReq studyTimeRecordReq) {

        return trainingService.updateStudyTimeRecord(studyTimeRecordReq);
    }

    @PostMapping("/ending")
    public Boolean ending(@Validated @RequestBody StudyTimeRecordReq studyTimeRecordReq) {
        return trainingService.ending(studyTimeRecordReq);
    }

    @PostMapping("/start")
    public StudyTimeRecordResp start(@Validated @RequestBody StudyTimeRecordReq studyTimeRecordReq) {
        return trainingService.startRecord(studyTimeRecordReq);
    }

    /**
     * 更新培训计划审核状态
     * 
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/updateInvigilate")
    public Boolean updateInvigilate(@RequestParam Long id, @RequestParam Long status) {
        TrainingReq trainingReq = new TrainingReq();
        trainingReq.setStatus(status);
        trainingService.update(trainingReq, id);
        //1.更新培训视频的审核状态为已审核
        TrainingDO trainingDO1 = new TrainingDO();
        trainingDO1.setId(id);
        trainingDO1.setStatus(1L);
        trainingMapper.updateById(trainingDO1);
        return true;
    }

    @GetMapping("/experts")
    public List<ExpertVO> experts() {
        return trainingService.experts();
    }
}
