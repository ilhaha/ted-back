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

import top.continew.admin.exam.model.vo.PlanLocationAndRoomVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.exam.model.query.ExamLocationQuery;
import top.continew.admin.exam.model.req.ExamLocationReq;
import top.continew.admin.exam.model.resp.ExamLocationDetailResp;
import top.continew.admin.exam.model.resp.ExamLocationResp;

import java.util.List;

/**
 * 考试地点业务接口
 *
 * @author Anton
 * @since 2025/03/11 15:04
 */
public interface ExamLocationService extends BaseService<ExamLocationResp, ExamLocationDetailResp, ExamLocationQuery, ExamLocationReq> {

    /**
     * 根据计划id获取计划对应的考试地点和考场信息
     * @param planId
     * @return
     */
    List<PlanLocationAndRoomVO> getPlanLocationAndRoomByPlanId(Long planId);

    /**
     * 分页查询
     * 更新
     */

    //    public Page<ExamLocationResp> page(PageQuery pageQuery);

}