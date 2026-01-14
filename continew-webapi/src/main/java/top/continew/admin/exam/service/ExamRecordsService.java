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

package top.continew.admin.exam.service;

import org.springframework.http.ResponseEntity;
import top.continew.admin.exam.model.entity.ExamRecordsDO;
import top.continew.admin.exam.model.req.GenerateReq;
import top.continew.admin.exam.model.req.InputScoresReq;
import top.continew.admin.exam.model.vo.CandidatesClassRoomVo;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamRecordsQuery;
import top.continew.admin.exam.model.req.ExamRecordsReq;
import top.continew.admin.exam.model.resp.ExamRecordsDetailResp;
import top.continew.admin.exam.model.resp.ExamRecordsResp;

import java.util.List;

/**
 * 考试记录业务接口
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
public interface ExamRecordsService extends BaseService<ExamRecordsResp, ExamRecordsDetailResp, ExamRecordsQuery, ExamRecordsReq> {
    PageResp<ExamRecordsResp> examRecordsPage(ExamRecordsQuery query, PageQuery pageQuery);

    ExamRecordsDetailResp getRecordsById(Long id);

    /**
     * 根据身份证号获取考生所有的考场
     * 
     * @param username
     * @return
     */
    List<CandidatesClassRoomVo> getCandidatesClassRoom(String username);

    void candidatesAdd(ExamRecordsDO examRecordsDO);

    /**
     * 录入实操、道路成绩
     * 
     * @param inputScoresReq
     * @return
     */
    Boolean inputScores(InputScoresReq inputScoresReq);

    /**
     * 生成资格证书
     * 
     * @param generateReq
     * @return
     */
    Boolean generateQualificationCertificate(GenerateReq generateReq);

    /**
     * 下载资格证书
     * 
     * @param recordIds
     * @return
     */
    ResponseEntity<byte[]> downloadQualificationCertificate(List<Long> recordIds, Integer planType);

}