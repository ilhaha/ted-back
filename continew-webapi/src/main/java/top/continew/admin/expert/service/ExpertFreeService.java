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

package top.continew.admin.expert.service;

import top.continew.admin.expert.model.*;
import top.continew.admin.util.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Anton
 * @date 2025/4/27-11:29
 */
public interface ExpertFreeService {

    /**
     * 查询专家费用By机构Id
     * 
     * @return
     */
    ExpertFreeRespTotalResp queryExpertFree(int pageSize, int curSize);

    /**
     * 查询某段时间内的需要支付的专家的钱
     * 
     * @param begin
     * @param end
     * @return
     */
    List<ExpertFreeShouldResp> queryExpertShouldPay(LocalDateTime begin, LocalDateTime end);

    List<ExpertFreelistResp> getExpertFreeById(Long id);

    Result addExpert(Expert expert);

    ExpertFreeList getExpert(Long id);

    Result updateExpert(Long id, Expert expert);

    Result updateExpertFree(Long id, ExpertFreelistResp expert);

    List<ExpertFreeShouldResp> exportSelectedExperts(ExpertFreeShouldReq request);
}
