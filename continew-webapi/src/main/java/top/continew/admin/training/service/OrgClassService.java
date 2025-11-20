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

import top.continew.admin.training.model.vo.SelectClassVO;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgClassQuery;
import top.continew.admin.training.model.req.OrgClassReq;
import top.continew.admin.training.model.resp.OrgClassDetailResp;
import top.continew.admin.training.model.resp.OrgClassResp;

import java.util.List;

/**
 * 培训机构班级业务接口
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
public interface OrgClassService extends BaseService<OrgClassResp, OrgClassDetailResp, OrgClassQuery, OrgClassReq> {
    /**
     * 根据项目类型和班级类型获取班级选择器
     * 
     * @param projectId
     * @param classType
     * @return
     */
    List<SelectClassVO> getSelectClassByProject(Long projectId, Integer classType);
}