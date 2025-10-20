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
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.training.model.entity.OrgDO;
import top.continew.admin.training.model.entity.TedOrgUser;
import top.continew.starter.data.mp.base.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Anton
 * @date 2025/5/14-9:11
 */
@Mapper
public interface OrgUserMapper extends BaseMapper<TedOrgUser> {
    @Select("select tou.org_id  from ted_org_user as tou  where tou.user_id = #{userId} and tou.is_deleted = 0")
    Long selectByUserId(String userId);

    @Select("""
    <script>
        SELECT 
            tou.org_id,
            su.username,
            su.nickname
        FROM ted_org_user tou
        LEFT JOIN sys_user su ON su.id = tou.user_id
        WHERE tou.is_deleted = 0
          AND tou.org_id IN 
          <foreach collection='orgIds' item='id' open='(' separator=',' close=')'>
            #{id}
          </foreach>
    </script>
""")
    List<Map<String, Object>> listAccountNamesByOrgIds(@Param("orgIds") List<Long> orgIds);


    /**
     * 根据用户id获取机构信息
     * @param userId
     * @return
     */
    @Select("""
    <script>
       SELECT org.*
       FROM ted_org org
       LEFT JOIN ted_org_user tou
       ON org.id = tou.org_id AND tou.user_id = #{userId} AND tou.is_deleted = 0
       WHERE org.is_deleted = 0
    </script>
""")
    OrgDO selectOrgByUserId(@Param("userId") Long userId);
}
