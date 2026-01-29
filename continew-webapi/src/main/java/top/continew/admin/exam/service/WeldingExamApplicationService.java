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

import top.continew.admin.exam.model.req.ReviewWeldingExamApplicationReq;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.WeldingExamApplicationQuery;
import top.continew.admin.exam.model.req.WeldingExamApplicationReq;
import top.continew.admin.exam.model.resp.WeldingExamApplicationDetailResp;
import top.continew.admin.exam.model.resp.WeldingExamApplicationResp;

/**
 * 机构申请焊接考试项目业务接口
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
public interface WeldingExamApplicationService extends BaseService<WeldingExamApplicationResp, WeldingExamApplicationDetailResp, WeldingExamApplicationQuery, WeldingExamApplicationReq> {
    /**
     * 审核
     * 
     * @param req
     * @return
     */
    Boolean review(ReviewWeldingExamApplicationReq req);
}