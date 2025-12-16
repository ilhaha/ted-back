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

package top.continew.admin.exam.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ilhaha
 * @Create 2025/10/16 15:31
 *
 *         监考员获取监考的计划信息
 */
@Data
public class InvigilateExamPlanVO {

    /**
     * 考试计划id
     */
    private Long planId;

    /**
     * 考试计划名称
     */
    private String planName;

    /**
     * 考试项目id
     */
    private Long projectId;

    /**
     * 考试项目
     */
    private String projectName;

    /**
     * 项目代号
     */
    private String projectCode;

    /**
     * 考试时长(分钟)
     */
    private Integer examDuration;

    /**
     * 八大类
     */
    private String categoryName;

    /**
     * 考试开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime startTime;


    /**
     * 考试人员类型
     */
    private Integer planType;

    /**
     * 监考状态
     */
    private Integer invigilateStatus;


    /**
     * 监考考场名称
     */
    private String classroomName;

    /**
     * 监考考场id
     */
    private String classroomId;

    /**
     * 考场类型
     */
    private Integer examType;



}
