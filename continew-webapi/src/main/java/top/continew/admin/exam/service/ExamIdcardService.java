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
import top.continew.admin.exam.model.query.ExamIdcardQuery;
import top.continew.admin.exam.model.req.ExamIdcardReq;
import top.continew.admin.exam.model.resp.ExamIdcardDetailResp;
import top.continew.admin.exam.model.resp.ExamIdcardResp;

/**
 * 考生身份证信息业务接口
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
public interface ExamIdcardService extends BaseService<ExamIdcardResp, ExamIdcardDetailResp, ExamIdcardQuery, ExamIdcardReq> {

    /**
     * 考生根据身份证号查看是否已实名
     * 
     * @param username
     * @return
     */
    Boolean verifyRealName(String username);

    /**
     * 添加实名认证
     * 
     * @param examIdcardReq
     * @return
     */
    Long saveRealName(ExamIdcardReq examIdcardReq);
}