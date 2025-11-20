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

import top.continew.admin.training.model.query.OrgCategoryRelationQuery;
import top.continew.admin.training.model.req.OrgCategoryRelationReq;
import top.continew.admin.training.model.resp.OrgCategoryRelationDetailResp;
import top.continew.admin.training.model.resp.OrgCategoryRelationResp;
import top.continew.starter.extension.crud.service.BaseService;

/**
 * 机构与八大类关联，记录多对多关系业务接口
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
public interface OrgCategoryRelationService extends BaseService<OrgCategoryRelationResp, OrgCategoryRelationDetailResp, OrgCategoryRelationQuery, OrgCategoryRelationReq> {}