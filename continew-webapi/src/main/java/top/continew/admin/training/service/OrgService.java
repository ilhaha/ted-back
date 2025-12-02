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

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.training.model.req.OrgApplyReq;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.model.vo.*;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.training.model.query.OrgQuery;
import top.continew.admin.training.model.req.OrgReq;
import top.continew.admin.training.model.resp.OrgDetailResp;
import top.continew.admin.training.model.resp.OrgResp;

import java.util.List;
import java.util.Map;

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
     * 获取当前用户在指定机构的状态（包含状态码和备注）
     * 
     * @param orgId 机构ID
     * @return 包含status和remark的VO对象
     */
    AgencyStatusVO getAgencyStatus(Long orgId);

    /**
     * 学生报名机构
     *
     * @return Integer
     */
    Integer studentAddAgency(Long orgId, Long projectId);

    /**
     * 学生退出机构
     *
     * @return Integer
     */
    Integer studentQuitAgency(Long orgId);

    /**
     * 机构移除学生
     *
     * @param orgId       机构ID
     * @param candidateId 学生ID
     * @return Integer
     */
    Integer agencyRemoveStudent(Long orgId, Long candidateId);

    /**
     * 学生取消报名机构
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
     * 
     * @param orgId
     * @return
     */
    Boolean unbindUserToOrg(Long orgId);

    /**
     * 获取机构对应的分类-项目级联选择
     * 
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
     * 
     * @param orgId
     * @return
     */
    List<ProjectCategoryVO> getSelectCategoryProjectClass(Long orgId);

    /**
     * 获取机构对应的项目-班级级联选择
     * 
     * @return
     */
    List<ProjectCategoryVO> getSelectProjectClass(Long orgId, Long projectId, Integer classType);

    /**
     * 根据报考状态获取机构对应的项目-班级-考生级联选择 （预报名）
     * 
     * @param projectId 项目id
     * @return
     */
    List<ProjectCategoryVO> getSelectProjectClassCandidate(Long projectId, Integer planType, Long planId);

    /**
     * 机构给作业人员报名
     * 
     * @return
     */
    Boolean apply(OrgApplyReq orgApplyPreReq);

    /**
     * 获取所有的机构作为选择器返回
     * 
     * @return
     */
    List<SelectOrgVO> getOrgSelect();

    /**
     * 根据班级类型获取机构对应的项目-班级级联选择
     * 
     * @return
     */
    List<Map<String, Object>> getSelectProjectClassByType(Integer type);

    /**
     * 获取班级类型机构对应的项目-班级级联选择
     * 
     * @return
     */
    List<ProjectCategoryVO> getSelectOrgProjectClassByType(Integer type);

    /**
     * 根据班级id下载导入作业人员模板
     * 
     * @param classId
     * @return
     */
    ResponseEntity<byte[]> downloadImportWorkerTemplate(Long classId);

    /**
     * 批量导入作业人员
     * 
     * @param file
     * @return
     */
    ParsedExcelResultVO importWorker(MultipartFile file, Long classId);

    /**
     * 解析导入作业人员Excel
     * @param file
     * @param classId
     * @return
     */
    ExcelParseResultVO parsedWorkerExcel(MultipartFile file, Long classId);
}