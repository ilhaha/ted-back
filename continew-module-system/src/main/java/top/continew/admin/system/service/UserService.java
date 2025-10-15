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

package top.continew.admin.system.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.auth.model.dto.ClassroomDTO;
import top.continew.admin.auth.model.dto.InvigilatorPlanDTO;
import top.continew.admin.auth.model.req.CandidatesExamPlanReq;
import top.continew.admin.auth.model.resp.CandidatesExamPlanVo;
import top.continew.admin.common.model.entity.UserRoleDeptDo;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.query.UserQuery;
import top.continew.admin.system.model.req.user.*;
import top.continew.admin.system.model.resp.user.UserDetailResp;
import top.continew.admin.system.model.resp.user.UserImportParseResp;
import top.continew.admin.system.model.resp.user.UserImportResp;
import top.continew.admin.system.model.resp.user.UserResp;
import top.continew.admin.system.model.vo.InvigilatorVO;
import top.continew.admin.system.model.vo.StudentDocumentTypeVO;
import top.continew.starter.data.mp.service.IService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseService;

import java.io.IOException;
import java.util.List;

/**
 * 用户业务接口
 *
 * @author Charles7c
 * @since 2022/12/21 21:48
 */
public interface UserService extends BaseService<UserResp, UserDetailResp, UserQuery, UserReq>, IService<UserDO> {

    /**
     * 下载导入模板
     *
     * @param response 响应对象
     * @throws IOException /
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 解析导入数据
     *
     * @param file 导入文件
     * @return 解析结果
     */
    UserImportParseResp parseImport(MultipartFile file);

    /**
     * 导入数据
     *
     * @param req 导入信息
     * @return 导入结果
     */
    UserImportResp importUser(UserImportReq req);

    /**
     * 重置密码
     *
     * @param req 重置信息
     * @param id  ID
     */
    void resetPassword(UserPasswordResetReq req, Long id);

    /**
     * 修改角色
     *
     * @param updateReq 修改信息
     * @param id        ID
     */
    void updateRole(UserRoleUpdateReq updateReq, Long id);

    /**
     * 上传头像
     *
     * @param avatar 头像文件
     * @param id     ID
     * @return 新头像路径
     * @throws IOException /
     */
    String updateAvatar(MultipartFile avatar, Long id) throws IOException;

    /**
     * 修改基础信息
     *
     * @param req 修改信息
     * @param id  ID
     */
    void updateBasicInfo(UserBasicInfoUpdateReq req, Long id);

    /**
     * 修改密码
     *
     * @param oldPassword 当前密码
     * @param newPassword 新密码
     * @param id          ID
     */
    void updatePassword(String oldPassword, String newPassword, Long id);

    /**
     * 修改手机号
     *
     * @param newPhone    新手机号
     * @param oldPassword 当前密码
     * @param id          ID
     */
    void updatePhone(String newPhone, String oldPassword, Long id);

    /**
     * 修改邮箱
     *
     * @param newEmail    新邮箱
     * @param oldPassword 当前密码
     * @param id          ID
     */
    void updateEmail(String newEmail, String oldPassword, Long id);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserDO getByUsername(String username);

    /**
     * 根据手机号查询
     *
     * @param phone 手机号
     * @return 用户信息
     */
    UserDO getByPhone(String phone);

    /**
     * 根据邮箱查询
     *
     * @param email 邮箱
     * @return 用户信息
     */
    UserDO getByEmail(String email);

    /**
     * 根据部门 ID 列表查询
     *
     * @param deptIds 部门 ID 列表
     * @return 用户数量
     */
    Long countByDeptIds(List<Long> deptIds);

    UserRoleDeptDo getUserRoleDeptByUserId(Long userId);

    /**
     * 查找所有同一个部门的监考人员
     * 
     * @param examPlanId 计划id
     * @param nickname   查询条件
     * @return 监考人员
     */
    PageResp<InvigilatorVO> getInvigilates(UserQuery query, PageQuery pageQuery, Long examPlanId, String nickname);

    /**
     * 查看监考人员
     * 
     * @param examPlanId    计划id
     * @param invigilateIds 监考id
     * @param startTime     监考开始时间
     * @param endTime       监考结束时间
     * @return 返回
     */
    List<InvigilatorVO> viewInvigilate(Long examPlanId,
                                       Long classroomId,
                                       List<Long> invigilateIds,
                                       String startTime,
                                       String endTime);

    /**
     * 获取已在监考的监考人员
     * 
     * @param examPlanId 计划id
     * @return 监考人id
     */
    List<Long> getExistInvigilates(long examPlanId);

    /**
     * 添加监考人员
     * 
     * @param examPlanId     计划id
     * @param invigilatorIds 监考人员id
     * @return 状态
     */
    int addInvigilates(Integer examPlanId, Integer classroomId, List<Long> invigilatorIds);

    /**
     * 删除监考时间
     * 
     * @param examPlanId   计划id
     * @param invigilateId 监考人id
     * @return 状态
     */
    int deleteInvigilateTime(Integer examPlanId, String invigilateId);

    /**
     * 根据身份证和准考证获取考试信息
     * 
     * @param candidatesExamPlanReq
     * @return
     */
    CandidatesExamPlanVo getPlanInfo(CandidatesExamPlanReq candidatesExamPlanReq);

    /**
     * 监考人标签
     * 
     * @param invigilateIds 监考人id
     * @return 返回值
     */
    List<InvigilatorVO> invigilateTag(List<Long> invigilateIds);

    /**
     * 忘记密码
     * 
     * @param phone    手机号码
     * @param password 密码
     */
    boolean forgotPassword(String phone, String password);

    /**
     * 分页查询考试计划考生列表
     * 
     * @param pageQuery 分页参数
     * @param nickname  昵称
     * @return 返回值
     */
    PageResp<UserResp> getStudentList(PageQuery pageQuery, UserQuery query, String nickname, Long orgId);

    /**
     * 获取考生证件类型状态
     * 
     * @param documentTypeList 证件类型集合
     * @return 状态
     */
    List<StudentDocumentTypeVO> getStudentDocumentTypeStatus(List<String> documentTypeList);

    /**
     * 判断考生是否参加过这场考试
     * 
     * @param candidateId
     * @param planId
     * @return
     */
    Long getExamRecord(Long candidateId, Long planId);

    /**
     * 根据开考密码获取考试计划
     * 
     * @param examPassword
     * @return
     */
    List<InvigilatorPlanDTO> getPlanInfoByExamPassword(String examPassword);

    ClassroomDTO getClassroomInfo(Long classroomId);

    void examBegins(Integer status, Long planId);

    /**
     * 验证监考人员考场重复
     * 
     * @param examPlanId
     * @param classroomId
     * @param invigilatorIds
     * @return
     */
    List<InvigilatorVO> verifyInvigilate(Long examPlanId, Long classroomId, List<Long> invigilatorIds);

    boolean findIsAccount(String username);

    boolean isPhoneExists(String phone, Long id);

    UserDO getOrg(Long userId);

    /**
     * 验证机构账号是否存在
     *
     * @param id
     */
    boolean checkOrg(Long id);
}
