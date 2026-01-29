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

package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 考生-考试项目考试状态实体
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Data
@TableName("ted_candidate_exam_project")
public class CandidateExamProjectDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 考试项目ID
     */
    private Long projectId;

    /**
     * 当前考试轮次（0表示未开始）
     */
    private Integer attemptNo;

    /**
     * 当前轮次是否已补考：0-否 1-是
     */
    private Boolean usedMakeup;

    /**
     * 是否通过：0-未通过 1-通过
     */
    private Boolean passed;

    /**
     * 通过时间
     */
    private LocalDateTime passTime;

    /**
     * 证书过期时间
     */
    private LocalDateTime certificateExpireTime;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}