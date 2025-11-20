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
import top.continew.admin.training.model.query.OrgTrainingPriceQuery;
import top.continew.admin.training.model.req.OrgTrainingPriceReq;
import top.continew.admin.training.model.resp.OrgTrainingPriceDetailResp;
import top.continew.admin.training.model.resp.OrgTrainingPriceResp;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）业务接口
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
public interface OrgTrainingPriceService extends BaseService<OrgTrainingPriceResp, OrgTrainingPriceDetailResp, OrgTrainingPriceQuery, OrgTrainingPriceReq> {

}