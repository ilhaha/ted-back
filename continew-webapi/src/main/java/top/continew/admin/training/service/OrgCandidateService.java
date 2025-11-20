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

package top.continew.admin.training.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgCandidateQuery;
import top.continew.admin.training.model.req.OrgCandidateReq;
import top.continew.admin.training.model.resp.OrgCandidateDetailResp;
import top.continew.admin.training.model.resp.OrgCandidateResp;

/**
 * 机构考生关联业务接口
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
public interface OrgCandidateService extends BaseService<OrgCandidateResp, OrgCandidateDetailResp, OrgCandidateQuery, OrgCandidateReq> {

    /**
     * 机构审核考生加入机构
     * 
     * @param orgCandidateReq
     * @return
     */
    Boolean review(OrgCandidateReq orgCandidateReq);
}