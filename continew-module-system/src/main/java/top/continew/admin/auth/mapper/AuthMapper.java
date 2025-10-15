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

package top.continew.admin.auth.mapper;

import org.apache.ibatis.annotations.*;
import top.continew.admin.system.model.req.user.UserOrgDTO;

@Mapper
public interface AuthMapper {
    @Insert("INSERT INTO sys_user (username, password, nickname,dept_id) " + "VALUES (#{username}, #{password}, #{nickname},#{deptId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long orgSignUp(UserOrgDTO userOrgDTO);

    @Insert("INSERT INTO sys_user_role (role_id, user_id) " + "VALUES (#{roleId}, #{candidateId})")
    void orgUserRoleAdd(Long roleId, Long candidateId);

    /*
     * 插入关联表
     */
    @Insert("INSERT INTO ted_org_candidate (candidate_id, org_id,status) " + "VALUES (#{candidateUserId}, #{orgId},#{status})")
    void linkCandidateWithOrg(@Param("candidateUserId") Long candidateUserId,
                              @Param("orgId") Long orgId,
                              @Param("status") Long status);

    @Select("SELECT id FROM sys_user WHERE username = #{username}")
    Long selectIdByUsername(String username);
}
