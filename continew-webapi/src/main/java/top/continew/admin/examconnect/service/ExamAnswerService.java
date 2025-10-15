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

package top.continew.admin.examconnect.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.examconnect.model.query.ExamAnswerQuery;
import top.continew.admin.examconnect.model.req.ExamAnswerReq;
import top.continew.admin.examconnect.model.resp.ExamAnswerDetailResp;
import top.continew.admin.examconnect.model.resp.ExamAnswerResp;

/**
 * 考生答题，记录考生答题情况业务接口
 *
 * @author Anton
 * @since 2025/04/07 10:41
 */
public interface ExamAnswerService extends BaseService<ExamAnswerResp, ExamAnswerDetailResp, ExamAnswerQuery, ExamAnswerReq> {}