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

import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.training.model.req.OrgApplyPreReq;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.model.vo.OrgProjectClassCandidateVO;
import top.continew.admin.training.model.vo.ProjectCategoryVO;
import top.continew.admin.training.model.vo.UserVO;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgQuery;
import top.continew.admin.training.model.req.OrgReq;
import top.continew.admin.training.model.resp.OrgDetailResp;
import top.continew.admin.training.model.resp.OrgResp;

import java.util.List;

/**
 * 机构信息业务接口
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
public interface OrgService extends BaseService<OrgResp, OrgDetailResp, OrgQuery, OrgReq> {
    public void orgSignUp(UserOrgDTO userDTO);

    public List<UserOrgDTO> processUserCredentials(List<UserOrgDTO> userDTOList);

    //批量存入redis
    public void batchSaveRedis(List<UserOrgDTO> userDTOList);

    public PageResp<OrgCandidatesResp> getCandidateList(OrgQuery query, PageQuery pageQuery, String type);

    /**
     * 获取所有机构信息
     * 
     * @return List<OrgResp>
     */
    PageResp<OrgResp> getAllOrgInfo(OrgQuery orgQuery, PageQuery pageQuery, String orgStatus);

    /**
     * 获取机构详情
     * 
     * @param orgId 机构id
     * @return OrgDetailResp
     */
    OrgDetailResp getOrgDetail(Long orgId);

    /**
     * 获取机构状态
     * 
     * @param orgId 机构id
     * @return Integer
     */
    Integer getAgencyStatus(Long orgId);

    /**
     * 学生报名
     * 
     * @return Integer
     */
    Integer studentAddAgency(Long orgId, Long projectId);

    /**
     * 学生取消报名
     * 
     * @param orgId 机构id
     * @return Integer
     */
    Integer studentDelAgency(Long orgId);

    /**
     * 通过加入申请
     * 
     * @param userId 用户id
     * @return Integer
     */
    Integer approveStudent(Long orgId, Long userId);

    /**
     * 拒绝加入申请
     * 
     * @param orgId  机构id
     * @param userId 用户id
     * @return Integer
     */
    Integer refuseStudent(Long orgId, Long userId);

    /**
     * 获取机构id
     * 
     * @param planId 考试计划id
     * @return Long
     */
    Long getOrgId(Long planId);

    OrgDetailResp getOrgInfo();

    List<String> getOrgAccounts(String id);

    void bindUserToOrg(String orgId, String userId);

    List<UserVO> getBindableUsers();

    /**
     * 解绑机构用户
     * @param orgId
     * @return
     */
    Boolean unbindUserToOrg(Long orgId);

    /**
     * 获取机构对应的分类-项目级联选择
     * @return
     */
    List<ProjectCategoryVO> getSelectCategoryProject(Long orgId);

    /**
     * 删除机构与用户关联信息
     *
     * @param orgId 机构ID
     * @return Boolean 删除是否成功
     */
    Boolean deleteOrgUserRelation(Long orgId);

    /**
     * 删除机构及其关联的用户信息（级联删除）
     *
     * @param orgId 机构ID
     */
    void removeOrgWithRelations(Long orgId);

    /**
     * 获取机构对应的分类-项目-班级级联选择
     * @param orgId
     * @return
     */
    List<ProjectCategoryVO> getSelectCategoryProjectClass(Long orgId);

    /**
     * 获取机构对应的项目-班级级联选择
     * @return
     */
    List<ProjectCategoryVO> getSelectProjectClass(Long orgId, Long projectId);

    /**
     * 根据报考状态获取机构对应的项目-班级-考生级联选择 （预报名）
     * @param projectId 项目id
     * @return
     */
    List<ProjectCategoryVO> getSelectProjectClassCandidate(Long projectId);

    /**
     * 机构预报名
     * @return
     */
    Boolean applyPre(OrgApplyPreReq orgApplyPreReq);

}