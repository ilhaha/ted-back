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

/**
 * 考试劳务费配置实体
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@TableName("ted_exam_violation")
public class ExamViolationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 违规描述
     */
    private String violationDesc;

    /**
     * 违规图片
     */
    private String illegalUrl;

    /**
     * 逻辑删除
     */
    private Integer isDeleted;

}