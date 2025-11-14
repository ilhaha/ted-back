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

package top.continew.admin.examconnect.model.query;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 题库，存储各类题目及其分类信息查询条件
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
@Data
@Schema(description = "题库，存储各类题目及其分类信息查询条件")
public class QuestionBankQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 八大类名称
     */
    @Schema(description = "八大类名称")
    @Query(type = QueryType.LIKE, columns = "t2.name")
    private String categoryName;

    /**
     * 八大类名称
     */
    @Schema(description = "知识类型名称")
    @Query(type = QueryType.LIKE, columns = "t4.name")
    private String knowledgeTypeName;

    /**
     * 八大类名称
     */
    @Schema(description = "所属项目")
    @Query(type = QueryType.LIKE, columns = "t3.project_name")
    private String projectName;

    /**
     * 八大类名称
     */
    @Schema(description = "题目标题")
    @Query(type = QueryType.LIKE, columns = "t1.question")
    private String question;



    /**
     * 考试类型（0-未指定，1-作业人员考试，2-无损/有损检验人员考试，可后续扩展）
     */
    @Schema(description = "考试类型")
    @Query(type = QueryType.LIKE, columns = "t1.exam_type")
    private Long examType;
}