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

package top.continew.admin.invigilate.service;

import top.continew.admin.invigilate.model.entity.Grades;
import top.continew.admin.invigilate.model.entity.TedExamRecords;
import top.continew.admin.invigilate.model.req.ExamScoreSubmitReq;
import top.continew.admin.invigilate.model.req.UpdateReviewReq;
import top.continew.admin.invigilate.model.resp.*;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.invigilate.model.query.PlanInvigilateQuery;
import top.continew.admin.invigilate.model.req.PlanInvigilateReq;

import java.util.List;

/**
 * 考试计划监考人员关联业务接口
 *
 * @author Anton
 * @since 2025/04/24 10:57
 */
public interface PlanInvigilateService extends BaseService<PlanInvigilateResp, PlanInvigilateDetailResp, PlanInvigilateQuery, PlanInvigilateReq> {

    /**
     * 根据监考人员Id和监考状态降序分页查询数据
     *
     * @param invigilatorId
     * @param invigilateStatus
     * @param pageSize
     * @param currentPage
     * @return
     */
    ExamRespList pageByInvigilatorId(Long invigilatorId,
                                     Integer invigilateStatus,
                                     Integer pageSize,
                                     Integer currentPage);

    /**
     * 获取监考的考试详情
     *
     * @param invigilatorId
     * @param examId
     * @return
     */
    InvigilateExamDetailResp getInvigilateExamDetail(Long invigilatorId, Long examId);

    /**
     * 批量录入考试成绩
     *
     * @param examScoreSubmitReq
     */
    void enterGrades(ExamScoreSubmitReq examScoreSubmitReq);

    /**
     * 更新考试记录的审核状态
     *
     * @param examId
     * @param updateReviewReqs
     * @return
     */
    void updateReviewStatus(Long examId, List<UpdateReviewReq> updateReviewReqs);

    /**
     * 获取所有待录入和已录入和被拒绝的考试记录
     * 
     * @param examId
     * @return
     */
    List<Grades> queryAlreadyCommitOrReject(Long examId);

    /**
     * 查询需要审核的记录
     * 
     * @param examId
     * @return
     */
    List<Grades> queryNeedReviewByExamId(Long examId);

    /**
     * 更新学生成绩
     * 
     * @param tedExamRecords
     */
    void updateScoreRecord(TedExamRecords tedExamRecords);

    /**
     * 删除考试成绩记录（直接删除，不保留数据）
     * 
     * @param examId
     * @param candidateId
     */
    void deleteScoreRecord(Long examId, Long candidateId);

    String getPassword(Long examId);

    /**
     * 根据计划id获取计划分配的监考员信息
     * @param planId
     * @return
     */
    List<InvigilatorAssignResp> getListByPlanId(Long planId);
}