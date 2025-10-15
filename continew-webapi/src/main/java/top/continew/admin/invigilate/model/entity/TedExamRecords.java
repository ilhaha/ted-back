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

package top.continew.admin.invigilate.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ted_exam_records")
public class TedExamRecords {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 计划ID（对应数据库 plan_id）
     */
    private Long planId;

    /**
     * 考生ID（对应数据库 candidate_id）
     */
    private Long candidateId;

    /**
     * 考试进度：0-待考试；1-考试中；2-等待成绩；3-已完成
     */
    private Short registrationProgress;

    /**
     * 考试得分，格式为项目id_得分#项目id_得分#
     */
    private String examScores;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 创建时间（数据库默认 CURRENT_TIMESTAMP）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（数据库默认 CURRENT_TIMESTAMP，更新时自动刷新）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //    /**
    //     * 是否删除：0-未删除，1-已删除
    //     */
    //    @TableLogic
    //    private Integer isDeleted;

    /**
     * 考试作答试卷表
     */
    private String answerSheetUrl;

    /**
     * 审核状态：0-待审核；1-已审核
     */
    private Integer reviewStatus;
}