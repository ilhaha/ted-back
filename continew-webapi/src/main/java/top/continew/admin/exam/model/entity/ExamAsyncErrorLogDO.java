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
 * 考试异步任务错误日志实体
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
@Data
@TableName("ted_exam_async_error_log")
public class ExamAsyncErrorLogDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 报名ID
     */
    private Long enrollId;

    /**
     * 失败步骤
     */
    private String step;

    /**
     * 错误简要信息
     */
    private String errorMsg;

    /**
     * 异常堆栈信息
     */
    private String stackTrace;

    /**
     * 处理状态：0-未处理，1-已处理
     */
    private Integer status;

    /**
     * 删除标记(0未删,1已删)
     */
    private Integer isDeleted;
}