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

package top.continew.admin.exam.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamAsyncErrorLogQuery;
import top.continew.admin.exam.model.req.ExamAsyncErrorLogReq;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogDetailResp;
import top.continew.admin.exam.model.resp.ExamAsyncErrorLogResp;

/**
 * 考试异步任务错误日志业务接口
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
public interface ExamAsyncErrorLogService extends BaseService<ExamAsyncErrorLogResp, ExamAsyncErrorLogDetailResp, ExamAsyncErrorLogQuery, ExamAsyncErrorLogReq> {

    void recordError(Long planId, Long enrollId, String step, Exception e);
}