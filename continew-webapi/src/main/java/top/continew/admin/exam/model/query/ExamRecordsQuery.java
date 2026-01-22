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

package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考试记录查询条件
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Data
@Schema(description = "考试记录查询条件")
public class ExamRecordsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @Query(type = QueryType.EQ,columns = "ter.plan_id")
    private Long planId;

    /**
     * 计划名称
     */
    @Schema(description = "计划名称")
    @Query(type = QueryType.LIKE,columns = "tep.exam_plan_name")
    private String planName;

    /**
     * 考试项目
     */
    @Schema(description = "考试项目")
    @Query(type = QueryType.LIKE,columns = "tp.project_name")
    private String projectName ;

    /**
     * 班级id
     */
    @Schema(description = "班级id")
    @Query(type = QueryType.EQ, columns = "toc.id")
    private Long classId;

    /**
     * 报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;
     */
    @Schema(description = "报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;")
    @Query(type = QueryType.EQ)
    private Integer registrationProgress;

    /**
     * 审核进度；0:待审核;1:已审核;2:审核未通过;
     */
    @Schema(description = "审核进度；0:待审核;1:已审核;2:审核未通过;")
    @Query(type = QueryType.EQ)
    private Integer reviewStatus;

    /**
     * 证书状态
     */
    @Schema(description = "证书状态")
    @Query(type = QueryType.EQ)
    private Integer isCertificateGenerated;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @Query(type = QueryType.LIKE, columns = "su.nickname")
    private String candidateName;


    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @Query(type = QueryType.EQ, columns = "ter.candidate_id")
    private Long candidateId;

    /**
     * 考生身份证
     */
    @QueryIgnore
    private String username;

    /**
     * 是否是机构查询
     */
    @QueryIgnore
    private Boolean isOrgQuery;


}