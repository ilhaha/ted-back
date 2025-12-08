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

package top.continew.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Equivalence;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import top.continew.admin.auth.model.dto.ClassroomDTO;
import top.continew.admin.auth.model.dto.InvigilatorPlanDTO;
import top.continew.admin.auth.model.req.CandidatesExamPlanReq;
import top.continew.admin.auth.model.resp.CandidatesExamPlanVo;
import top.continew.admin.common.config.mybatis.DataPermissionMapper;
import top.continew.admin.common.constant.enums.ExamPlanStatusEnum;
import top.continew.admin.common.model.entity.UserRoleDeptDo;
import top.continew.admin.system.model.dto.UserDetailDTO;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.resp.user.UserDetailResp;
import top.continew.admin.system.model.resp.user.UserResp;
import top.continew.admin.system.model.vo.*;
import top.continew.starter.extension.datapermission.annotation.DataPermission;
import top.continew.starter.security.crypto.annotation.FieldEncrypt;

import java.util.List;

/**
 * 用户 Mapper
 *
 * @author Charles7c
 * @since 2022/12/22 21:47
 */
public interface UserMapper extends DataPermissionMapper<UserDO> {

    /**
     * 分页查询列表
     *
     * @param page         分页条件
     * @param queryWrapper 查询条件
     * @return 分页列表信息
     */
    @DataPermission(tableAlias = "t1")
    IPage<UserDetailResp> selectUserPage(@Param("page") IPage<UserDO> page,
                                         @Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    /**
     * 分页查询考务列表
     *
     * @param page         分页条件
     * @param queryWrapper 查询条件
     * @return 分页列表信息
     */

    IPage<UserDetailResp> selectExamStaffPage(
            Page<?> page,
            @Param(Constants.WRAPPER) Wrapper<UserDO> queryWrapper);



    /**
     * 查询列表
     *
     * @param queryWrapper 查询条件
     * @return 列表信息
     */
    @DataPermission(tableAlias = "t1")
    List<UserDetailResp> selectUserList(@Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    UserDO selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    UserDO selectByPhone(@FieldEncrypt @Param("phone") String phone);

    /**
     * 根据邮箱查询
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    UserDO selectByEmail(@FieldEncrypt @Param("email") String email);

    /**
     * 根据 ID 查询昵称
     *
     * @param id ID
     * @return 昵称
     */
    @Select("SELECT nickname FROM sys_user WHERE id = #{id}")
    String selectNicknameById(@Param("id") Long id);

    /**
     * 根据邮箱查询数量
     *
     * @param email 邮箱
     * @param id    ID
     * @return 用户数量
     */
    Long selectCountByEmail(@FieldEncrypt @Param("email") String email, @Param("id") Long id);

    /**
     * 根据手机号查询数量
     *
     * @param phone 手机号
     * @param id    ID
     * @return 用户数量
     */
    Long selectCountByPhone(@Param("phone") String phone, @Param("id") Long id);

    /**
     * 通过用户id查询部门 角色信息
     */
    @Select("SELECT t1.id as user_id, t4.parent_id as parent_dept_id,t4.name as dept_name, t4.id as deptId, t3.id as roleId, t3.name as roleName " + "FROM sys_user as t1 " + "LEFT JOIN sys_user_role as t2 ON t1.id = t2.user_id " + "LEFT JOIN sys_role as t3 ON t2.role_id = t3.id " + "LEFT JOIN sys_dept as t4 ON t1.dept_id = t4.id " + "WHERE t1.id = #{id}")
    UserRoleDeptDo selectUserRoleDeptByUserId(Long userId);

    /**
     * 获取已存在监考人员
     * 
     * @param examPlanId 计划id
     * @return 监考人员id
     */
    @Select("select invigilator_id " + "from ted_plan_invigilate " + "where exam_plan_id = #{examPlanId}")
    List<Long> getExistInvigilates(@Param("examPlanId") long examPlanId);

    /**
     * 添加监考人员时间
     * 
     * @param examPlanId     计划名称
     * @param invigilatorIds 监考人员id
     * @return 状态
     */
    int addInvigilatesTime(@Param("examPlanId") Integer examPlanId,
                           @Param("classroomId") Integer classroomId,
                           @Param("invigilatorIds") List<Long> invigilatorIds);

    /**
     * 删除考试计划下的监考人员
     * 
     * @param examPlanId 计划id
     */
    @Delete("delete from ted_plan_invigilate " + "where exam_plan_id = #{ examPlanId } " + "and classroom_id = #{ classroomId }")
    void deleteInvigilate(@Param("examPlanId") int examPlanId, @Param("classroomId") int classroomId);

    /**
     * 删除监考人监考计划
     * 
     * @param examPlanId   计划id
     * @param invigilateId 监考id
     * @return 状态
     */
    @Delete("delete from ted_plan_invigilate " + "where exam_plan_id = #{examPlanId} " + "and invigilator_id = #{invigilateId}")
    int deleteInvigilateTime(Integer examPlanId, String invigilateId);

    @Select("SELECT " + "ted_enroll.user_id AS candidate_id, " + "ted_exam_plan.id AS plan_id, " + "ted_exam_plan.start_time, " + "ted_exam_plan.end_time, " + "ted_exam_plan.exam_plan_name AS plan_name, " + "ted_exam_plan.status AS status, " + "ted_enroll.enroll_status AS enrollStatus, " + "ted_enroll.exam_status AS examStatus, " + "ted_enroll.classroom_id AS classroomId, " + "ted_category.video_url AS warningShortFilm " + "FROM " + "ted_enroll " + "LEFT JOIN ted_exam_plan ON ted_enroll.exam_plan_id = ted_exam_plan.id " + "LEFT JOIN ted_project ON ted_exam_plan.exam_project_id = ted_project.id " + "LEFT JOIN ted_category ON ted_project.category_id = ted_category.id " + "WHERE " + "ted_enroll.user_id = #{candidateId} " + "AND ted_enroll.exam_number = #{examNumber} " + "AND enroll_status = #{enrollStatus}")
    CandidatesExamPlanVo getPlanInfo(CandidatesExamPlanReq candidatesExamPlanReq);

    @Select("select phone from sys_user")
    List<String> findLoginUserPhoneList();

    @Select("select p.dept_id from ted_exam_plan as ep " + "join ted_project as p " + "on ep.exam_project_id = p.id " + "where ep.id = #{examPlanId} and ep.status = #{examPlanStatus}")
    Long getDeptIdByExamPlanId(@Param("examPlanId") Long examPlanId,
                               @Param("examPlanStatus") ExamPlanStatusEnum examPlanStatus);

    List<InvigilatorVO> getInvigilatesAndTime(@Param("deptId") Long deptId,
                                              @Param("classroomId") Long classroomId,
                                              @Param("invigilatorId") Long invigilatorId,
                                              @Param("invigilateIds") List<Long> invigilateIds,
                                              @Param("examPlanStatusInForce") ExamPlanStatusEnum examPlanStatusInForce,
                                              @Param("examPlanStatusEnded") ExamPlanStatusEnum examPlanStatusEnded);

    IPage<InvigilatorVO> getInvigilates(@Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper,
                                        @Param("page") Page<UserDO> page);

    List<InvigilatorVO> listInvigilatorsByPlanId(@Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    @Select("select * from sys_user where phone = #{ phone }")
    UserDO getUserByPhone(@Param("phone") String phone);

    IPage<UserResp> getStudentList(@Param("page") Page<UserDO> page,
                                   @Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    @Select("select org_id from ted_org_user where user_id = #{userId} and is_deleted = 0")
    Long getUserOrgId(Long userId);

    List<StudentDocumentTypeVO> getStudentDocumentType(@Param("orgId") Long orgId,
                                                       @Param("candidatesId") Long candidatesId);

    List<InvigilatorPlanDTO> getPlanInfoByExamPassword(@Param("examPassword") String examPassword);

    Long getExamRecord(@Param("candidateId") Long candidateId, @Param("planId") Long planId);

    void examBegins(@Param("status") Integer status, @Param("planId") Long planId);

    ClassroomDTO getClassroomInfo(@Param("classroomId") Long classroomId);

    List<InvigilatorVO> verifyInvigilate(@Param("examPlanId") Long examPlanId,
                                         @Param("classroomId") Long classroomId,
                                         @Param("invigilatorIds") List<Long> invigilatorIds);

    @Select("select count(1) " + "from ted_plan_invigilate " + "where exam_plan_id = #{ examPlanId } " + "and classroom_id = #{ classroomId }")
    int getInvigilateCount(@Param("examPlanId") Integer examPlanId, @Param("classroomId") Integer classroomId);

    @Select("select * from sys_user where username = #{ username }")
    UserDO findIsAccount(String username);

    @Select("select * from sys_user   ${ew.customSqlSegment}")
    List<UserDO> selectListByIds(@Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    void deleteOrgUser(List<Long> ids);

    void deleteOrgCandidate(List<Long> ids);

    @Select("select u.* from sys_user as u " + "join sys_user_role ur on u.id = ur.user_id " + "join ted_org_user ou on u.id = ou.user_id " + "where ur.role_id = #{ organizationId } " + "and u.id = #{ userId } and ou.is_deleted = 0")
    UserDO getOrg(@Param("userId") Long userId, @Param("organizationId") Long organizationId);

    @Select("SELECT COUNT(1) FROM ted_org_user WHERE user_id = #{id}")
    boolean checkOrgUser(Long id);

    List<UploadedDocumentTypeVO> getUnuploadedDocumentTypes(@Param("candidateId") String candidateId,
                                                            @Param("planId") String planId);

    PlanInfoVO getPlanInfoByPlanId(@Param("planId") String planId);

    EnrollPreInfoVO getEnrollPreInfo(@Param("candidateId") String candidateId, @Param("planId") String planId);

    List<UploadedDocumentTypeVO> getUploadedDocumentTypes(@Param("uploadPreId") Long uploadPreId);

    int getCandidateIdentity(@Param("id") Long id);

    void updateExamStatus(@Param("candidateId") Long candidateId,
                          @Param("examNumberEncrypt") String examNumberEncrypt,
                          @Param("planId") Long planId,
                          @Param("examStatus") Integer examStatus);


    /**
     * 删除考务人员资质证明
     */
    @Delete("DELETE FROM ted_user_qualification WHERE user_id = #{userId}")
    void deleteUserQualificationsByUserId(@Param("userId") Long userId);



    /**
     * 根据ID查询用户详细信息
     */
    UserDetailDTO selectUserDetailById(@Param("id") Long id);
}
