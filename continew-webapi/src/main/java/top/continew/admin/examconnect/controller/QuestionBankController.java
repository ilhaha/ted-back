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

package top.continew.admin.examconnect.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import top.continew.admin.exam.model.vo.CascadeOptionsVo;
import top.continew.admin.examconnect.model.resp.ExamPaperVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.examconnect.model.query.QuestionBankQuery;
import top.continew.admin.examconnect.model.req.QuestionBankReq;
import top.continew.admin.examconnect.model.resp.QuestionBankDetailResp;
import top.continew.admin.examconnect.model.resp.QuestionBankResp;
import top.continew.admin.examconnect.service.QuestionBankService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 题库，存储各类题目及其分类信息管理 API
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Tag(name = "题库，存储各类题目及其分类信息管理 API")
@RestController
@CrudRequestMapping(value = "/examconnect/questionBank", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class QuestionBankController extends BaseController<QuestionBankService, QuestionBankResp, QuestionBankDetailResp, QuestionBankQuery, QuestionBankReq> {

    @Operation(summary = "生成考试计划试卷")
    @GetMapping("/generate/exam/{planId}")
    public Boolean generateExamQuestionBank(@PathVariable("planId") Long planId) {
        return baseService.generateExamQuestionBank(planId);
    }

    @Operation(summary = "获取考试题库")
    @GetMapping("/exam/{planId}")
    public ExamPaperVO getExamQuestionBank(@PathVariable("planId") Long planId) {
        return baseService.getExamQuestionBank(planId);
    }

    @GetMapping("/options")
    public List<CascadeOptionsVo> getOptions() {
        return baseService.getOptions();
    }
    @Operation(summary = "导出题目Excel")
    @GetMapping("/exportExcel")
    public void exportQuestions(@RequestParam Long categoryId,
                                @RequestParam Long subCategoryId,
                                @RequestParam Long knowledgeTypeId,
                                HttpServletResponse response) throws IOException {
        byte[] data = baseService.exportQuestionsExcel(categoryId, subCategoryId, knowledgeTypeId);
        String filename = URLEncoder.encode("题库导出.xlsx", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.getOutputStream().write(data);
    }
}