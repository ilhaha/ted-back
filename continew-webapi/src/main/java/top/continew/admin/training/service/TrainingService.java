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

package top.continew.admin.training.service;

import top.continew.admin.training.model.req.StudyTimeRecordReq;
import top.continew.admin.training.model.req.TrainTreeReq;
import top.continew.admin.training.model.resp.StudyTimeRecordResp;
import top.continew.admin.training.model.resp.TrainTreeResp;
import top.continew.admin.training.model.vo.ExpertVO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.TrainingQuery;
import top.continew.admin.training.model.req.TrainingReq;
import top.continew.admin.training.model.resp.TrainingDetailResp;
import top.continew.admin.training.model.resp.TrainingResp;

import java.util.List;

/**
 * 培训主表业务接口
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
public interface TrainingService extends BaseService<TrainingResp, TrainingDetailResp, TrainingQuery, TrainingReq> {
    TrainTreeResp getTree(TrainTreeReq trainTreeReq);

    TrainTreeResp getStuTree(TrainTreeReq trainTreeReq);

    PageResp<TrainingResp> getAllTraining(TrainingQuery query, PageQuery pageQuery);

    Boolean updateStudyTimeRecord(StudyTimeRecordReq studyTimeRecordReq);

    Boolean ending(StudyTimeRecordReq studyTimeRecordReq);

    StudyTimeRecordResp startRecord(StudyTimeRecordReq studyTimeRecordReq);

    List<ExpertVO> experts();
}