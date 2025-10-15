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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.starter.data.mp.base.BaseMapper;

/**
 * @author Anton
 * @date 2025/5/14-9:11
 */
@Mapper
public interface OrgUserMapper extends BaseMapper<TedOrgUser> {
    @Select("select tou.org_id  from ted_org_user as tou  where tou.user_id = #{userId}")
    Long selectByUserId(String userId);

}
