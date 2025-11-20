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

import jakarta.servlet.http.HttpServletResponse;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.TrainingCheckinQuery;
import top.continew.admin.training.model.req.TrainingCheckinReq;
import top.continew.admin.training.model.resp.TrainingCheckinDetailResp;
import top.continew.admin.training.model.resp.TrainingCheckinResp;

/**
 * 培训签到记录业务接口
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
public interface TrainingCheckinService extends BaseService<TrainingCheckinResp, TrainingCheckinDetailResp, TrainingCheckinQuery, TrainingCheckinReq> {
    String generateQRCode(Long trainingId);

    boolean doCheckin(String realName, String idCard, Long trainingId, Long orgId, Long ts, String sign);

    /**
     * 导出培训签到记录（Excel）
     */
    void exportExcel(TrainingCheckinQuery query, HttpServletResponse response);
}