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

package top.continew.admin.exam.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ilhaha
 * @Create 2025/12/24 16:13
 */
@Data
public class ExamRecordDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 报名进度;0:待考试;1:考试中;2:等待成绩;3:已完成;4:未上传指定资料;
     */
    private Integer registrationProgress;

    /**
     * 考试得分，(项目id_得分#项目id_得分#)
     */
    private Integer examScores;

    /**
     * 实操成绩
     */
    private Integer operScores;

    /**
     * 实操成绩录入状态（0未录入，1已录入）
     */
    private Integer operInputStatus;

    /**
     * 道路成绩
     */
    private Integer roadScores;

    /**
     * 道路成绩录入状态（0未录入，1已录入）
     */
    private Integer roadInputStatus;

    /**
     * 证书是否已生成（0：未生成，1：已生成）
     */
    private Integer isCertificateGenerated;

    /**
     * 考试结果（0不及格，1及格）
     */
    private Integer examResultStatus;

    /**
     * 考试类型（0首考、1补考）
     */
    private Integer attemptType;

    /**
     * 计划名称（后加）
     */
    private String planName;

    /**
     * 考生姓名（后加）
     */
    private String candidateName;

    /**
     * 考生身份证
     */
    private String username;

    /**
     * 考试试卷
     */
    private String examPaper;

    /**
     * 是否有实操考试
     */
    private Integer isOperation;

    /**
     * 是否有道路考试
     */
    private Integer isRoad;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 所属班级
     */
    private String className;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 焊接实操成绩（JSON 数组）
     */
    private String weldingOperScores;

    /**
     * 序号
     */
    private Integer seatId;

}
