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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.training.model.dto.OrgDTO;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.model.resp.OrgDetailResp;
import top.continew.admin.training.model.resp.OrgResp;
import top.continew.admin.training.model.vo.*;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.training.model.entity.OrgDO;

import java.util.List;
import java.util.Map;

/**
 * 机构信息 Mapper
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
public interface OrgMapper extends BaseMapper<OrgDO> {

    List<String> getStudentInfo(@Param("orgId") Long orgId);

    @Insert("INSERT INTO sys_user (username, password, nickname) " + "VALUES (#{username}, #{password}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long orgSignUp(UserOrgDTO userOrgDTO);

    /*
     * 插入关联表
     */
    @Insert("INSERT INTO ted_org_candidate (candidate_id, org_id) " + "VALUES (#{candidateId}, #{orgId})")
    Long linkCandidateWithOrg(@Param("candidateUserId") Long candidateUserId, @Param("orgId") Long orgId);

    OrgDTO getOrgId(@Param("userId") Long userId);

    public IPage<OrgCandidatesResp> getCandidatesList(@Param("page") Page<OrgDO> page,
                                                      @Param(Constants.WRAPPER) QueryWrapper<OrgDO> queryWrapper);

    IPage<OrgResp> getOrgList(@Param("page") Page<OrgDO> page,
                              @Param(Constants.WRAPPER) QueryWrapper<OrgDO> qw,
                              @Param("userId") Long userId,
                              @Param("bool") boolean bool);

    OrgDetailResp getOrgDetail(@Param("orgId") Long orgId);

    //  AgencyStatusVO，接收多字段
    AgencyStatusVO getAgencyStatus(@Param("orgId") Long orgId, @Param("userId") Long userId, @Param("userId") Long projectId);
    @Insert("insert into ted_org_candidate values (null, #{orgId}, #{userId}, #{projectId}, 1, null,1,1, now(), now() 0)")
    Integer studentAddAgency(@Param("orgId") Long orgId, @Param("userId") Long userId, @Param("projectId") Long projectId);

    @Select("select count(1) " + "from ted_org_candidate " + "where is_deleted = 0 " + "and status = 1 " + "and org_id = #{orgId} " + "and candidate_id = #{userId}")
    Integer findAgency(@Param("orgId") Long orgId, @Param("userId") Long userId);

    @Delete("update ted_org_candidate set is_deleted = 1,status = 3 where org_id = #{orgId} and candidate_id = #{userId}")
    Integer studentDelAgency(@Param("orgId") Long orgId, @Param("userId") Long userId );

    Integer studentQuitAgency(@Param("orgId") Long orgId, @Param("userId") Long userId);

    Integer studentQuitAgencyClass(@Param("orgId") Long orgId, @Param("userId") Long userId  );

    Integer approveStudent(@Param("orgId") Long orgId, @Param("userId") Long userId);

    Integer refuseStudent(@Param("orgId") Long orgId, @Param("userId") Long userId);

    OrgDetailResp selectByUserId(Long userId);

    List<String> getAcountInfo(String id);

    List<UserVO> getBindableUsers(@Param("organizationId") Long organizationId);

    List<ProjectCategoryVO> getSelectCategoryProject(@Param("userId") Long userId);

    List<ProjectCategoryVO> getSelectCategoryProjectByOrgId(@Param("orgId") Long orgId);

    List<ProjectCategoryVO> getAllCategoryByUserId(@Param("userId") Long userId);

    List<ProjectCategoryVO> getAllCategoryByOrgId(@Param("orgId") Long orgId);

    List<OrgProjectClassVO> getSelectProjectClass(@Param("orgId") Long orgId, @Param("projectId") Long projectId);

    List<OrgProjectClassCandidateVO> getSelectProjectClassCandidate(@Param("orgId") Long orgId,
                                                                    @Param("projectId") Long projectId,
                                                                    @Param("planType") Integer planType,
                                                                    @Param("planId") Long planId);

    List<SelectOrgVO> getOrgSelect();

    List<OrgProjectClassTypeVO> getSelectProjectClassByType(@Param("type") Integer type);

    List<OrgProjectClassVO> getSelectOrgProjectClassByType(@Param("orgId") Long orgId, @Param("type") Integer type);

    List<String> getNeedUploadDoc(@Param("classId") Long classId);
}