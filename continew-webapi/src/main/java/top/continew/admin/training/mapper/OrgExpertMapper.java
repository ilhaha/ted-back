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

package top.continew.admin.training.mapper;

import org.apache.ibatis.annotations.Select;
import top.continew.admin.training.model.entity.OrgExpertDO;
import top.continew.admin.training.model.vo.ExpertVO;
import top.continew.starter.data.mp.base.BaseMapper;

import java.util.List;

public interface OrgExpertMapper extends BaseMapper<OrgExpertDO> {

    @Select("SELECT te.id, te.name FROM ted_org_expert toe LEFT JOIN ted_expert te ON toe.expert_id = te.id WHERE toe.org_id = #{orgId} and toe.is_deleted=0  ")
    List<ExpertVO> listExperts(Long orgId);

    @Select("select o.name from ted_org_expert as oe " + "left join ted_org as o " + "on oe.org_id = o.id " + "where oe.expert_id = #{ id } " + "and oe.is_deleted = 0 " + "and o.is_deleted = 0")
    String getOrgName(Long id);
}
