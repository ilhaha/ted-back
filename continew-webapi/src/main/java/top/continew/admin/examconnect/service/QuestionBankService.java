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

package top.continew.admin.examconnect.service;

import top.continew.admin.exam.model.vo.CascadeOptionsVo;
import top.continew.admin.examconnect.model.req.RestPaperReq;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.examconnect.model.query.QuestionBankQuery;
import top.continew.admin.examconnect.model.req.QuestionBankReq;
import top.continew.admin.examconnect.model.resp.QuestionBankDetailResp;
import top.continew.admin.examconnect.model.resp.QuestionBankResp;

import java.io.IOException;
import java.util.List;

/**
 * 题库，存储各类题目及其分类信息业务接口
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
public interface QuestionBankService extends BaseService<QuestionBankResp, QuestionBankDetailResp, QuestionBankQuery, QuestionBankReq> {

    List<CascadeOptionsVo> getOptions();

    ExamPaperVO getExamQuestionBank(Long planId);

    /**
     * 生成考试计划试卷
     *
     * @param planId
     * @return
     */
    ExamPaperVO generateExamQuestionBank(Long planId);

    /**
     * 导出题库为 Excel 文件
     *
     * @param categoryId      八大类ID
     * @param subCategoryId   子类ID
     * @param knowledgeTypeId 知识类型ID
     * @return Excel 文件字节数组
     */
    byte[] exportQuestionsExcel(Long categoryId, Long subCategoryId, Long knowledgeTypeId) throws IOException;

    /**
     * 考生获取试卷
     * 
     * @param planId
     * @param userId
     * @return
     */
    ExamPaperVO getCandidatePaper(Long planId, Long userId);

    /**
     * 监考员重新生成考试试卷
     * 
     * @param restPaperReq
     * @return
     */
    Boolean restPaper(RestPaperReq restPaperReq);

    /**
     * 根据项目id删除题目
     * @param projectIds
     * @return
     */
    Boolean deleteByProjectIds(List<Long> projectIds);
}