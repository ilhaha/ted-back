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

package top.continew.admin.invigilate.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.invigilate.model.query.LaborFeeQuery;
import top.continew.admin.invigilate.model.req.LaborFeeReq;
import top.continew.admin.invigilate.model.resp.LaborFeeDetailResp;
import top.continew.admin.invigilate.model.resp.LaborFeeResp;

/**
 * 考试劳务费配置业务接口
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
public interface LaborFeeService extends BaseService<LaborFeeResp, LaborFeeDetailResp, LaborFeeQuery, LaborFeeReq> {
    /**
     * 更新劳务费状态
     *
     * @param req 劳务费实体
     * @return 更新结果
     */
    boolean toggleLaborFeeEnabled(LaborFeeReq req);
}