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

package top.continew.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ReUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.common.constant.RegexConstants;
import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.common.util.TokenLocalThreadUtil;
import top.continew.admin.invigilate.service.PlanInvigilateService;
import top.continew.admin.system.model.entity.UserDO;
import top.continew.admin.system.model.query.UserQuery;
import top.continew.admin.system.model.req.user.UserImportReq;
import top.continew.admin.system.model.req.user.UserPasswordResetReq;
import top.continew.admin.system.model.req.user.UserReq;
import top.continew.admin.system.model.req.user.UserRoleUpdateReq;
import top.continew.admin.system.model.resp.user.UserDetailResp;
import top.continew.admin.system.model.resp.user.UserImportParseResp;
import top.continew.admin.system.model.resp.user.UserImportResp;
import top.continew.admin.system.model.resp.user.UserResp;
import top.continew.admin.system.model.req.InvigilatorParameterReq;
import top.continew.admin.system.model.vo.InvigilatorVO;
import top.continew.admin.system.model.vo.StudentDocumentTypeVO;
import top.continew.admin.system.model.vo.UploadWhenUserInfoVO;
import top.continew.admin.system.service.UserService;
import top.continew.admin.training.service.OrgService;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.BaseIdResp;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.validation.CrudValidationGroup;

import java.io.IOException;
import java.util.List;

/**
 * 用户管理 API
 *
 * @author Charles7c
 * @since 2023/2/20 21:00
 */
@Tag(name = "用户管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/system/user", api = {Api.PAGE, Api.LIST, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
        Api.EXPORT})
public class UserController extends BaseController<UserService, UserResp, UserDetailResp, UserQuery, UserReq> {

    @Autowired
    private UserService userService;

    private final OrgService orgService;

    private final PlanInvigilateService planInvigilateService;

    @Resource
    private AESWithHMAC aesWithHMAC;

    @SaIgnore
    @GetMapping("/upload/when/info")
    @Operation(summary = "考生扫码上传资料时获取个人信息", description = "考生扫码上传资料时获取个人信息")
    public UploadWhenUserInfoVO uploadWhenInfo(@RequestParam("candidateId") String candidateId,@RequestParam("planId") String planId) {
        return userService.uploadWhenInfo(aesWithHMAC.verifyAndDecrypt(candidateId),aesWithHMAC.verifyAndDecrypt(planId));
    }

    @SaIgnore
    @GetMapping("/isPhoneExists")
    @Operation(summary = "判断手机是否已被绑定", description = "判断手机是否已被绑定")
    public Boolean isPhoneExists(String phone) {
        return userService.isPhoneExists(phone,null);
    }

    @GetMapping("/getUserByUserName")
    public UserDO getUserByUserName(String username) {
        String decryptionUsername = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(username));
        String aesEncryptionUsername = aesWithHMAC.encryptAndSign(decryptionUsername);
        return userService.getByUsername(aesEncryptionUsername);
    }

    @Override
    @Operation(summary = "新增数据", description = "新增数据")
    public BaseIdResp<Long> add(@Validated(CrudValidationGroup.Add.class) @RequestBody UserReq req) {
        String username = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getUsername()));
        ValidationUtils.throwIfNull(username, "用户名解密失败");
        String rawPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getPassword()));
        ValidationUtils.throwIfNull(rawPassword, "密码解密失败");
        ValidationUtils.throwIf(!ReUtil
                .isMatch(RegexConstants.PASSWORD, rawPassword), "密码长度为 8-32 个字符，支持大小写字母、数字、特殊字符，至少包含字母和数字");
        req.setPassword(rawPassword);
        req.setUsername(aesWithHMAC.encryptAndSign(username));
        return super.add(req);
    }

    @Operation(summary = "下载导入模板", description = "下载导入模板")
    @SaCheckPermission("system:user:import")
    @GetMapping(value = "/import/template", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        baseService.downloadImportTemplate(response);
    }

    @Operation(summary = "解析导入数据", description = "解析导入数据")
    @SaCheckPermission("system:user:import")
    @PostMapping("/import/parse")
    public UserImportParseResp parseImport(@NotNull(message = "文件不能为空") MultipartFile file) {
        ValidationUtils.throwIf(file::isEmpty, "文件不能为空");
        return baseService.parseImport(file);
    }

    @Operation(summary = "导入数据", description = "导入数据")
    @SaCheckPermission("system:user:import")
    @PostMapping(value = "/import")
    public UserImportResp importUser(@Validated @RequestBody UserImportReq req) {
        return baseService.importUser(req);
    }

    @Operation(summary = "重置密码", description = "重置用户登录密码")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @SaCheckPermission("system:user:resetPwd")
    @PatchMapping("/{id}/password")
    public void resetPassword(@Validated @RequestBody UserPasswordResetReq req, @PathVariable Long id) {
        String rawNewPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(req.getNewPassword()));
        ValidationUtils.throwIfNull(rawNewPassword, "新密码解密失败");
        ValidationUtils.throwIf(!ReUtil
                .isMatch(RegexConstants.PASSWORD, rawNewPassword), "密码长度为 8-32 个字符，支持大小写字母、数字、特殊字符，至少包含字母和数字");
        req.setNewPassword(rawNewPassword);
        baseService.resetPassword(req, id);
    }

    @Operation(summary = "分配角色", description = "为用户新增或移除角色")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @SaCheckPermission("system:user:updateRole")
    @PatchMapping("/{id}/role")
    public void updateRole(@Validated @RequestBody UserRoleUpdateReq updateReq, @PathVariable Long id) {
        baseService.updateRole(updateReq, id);
    }

    @GetMapping("/getInvigilates")
    public PageResp<InvigilatorVO> getInvigilates(@Validated UserQuery query,
                                                  @Validated PageQuery pageQuery,
                                                  InvigilatorParameterReq ipo) {
        return userService.getInvigilates(query, pageQuery, ipo.getExamPlanId(), ipo.getNickname());
    }

    @GetMapping("/viewInvigilate")
    public List<InvigilatorVO> viewInvigilate(InvigilatorParameterReq ipo) {
        return userService.viewInvigilate(ipo.getExamPlanId(), ipo.getClassroomId(), ipo.getInvigilateIds(), ipo
                .getStartTime(), ipo.getEndTime());
    }

    @GetMapping("/invigilateTag")
    public List<InvigilatorVO> invigilateTag(InvigilatorParameterReq ipo) {
        return userService.invigilateTag(ipo.getInvigilateIds());
    }

    @GetMapping("/getExistInvigilates")
    public List<Long> getExistInvigilates(long examPlanId) {
        return userService.getExistInvigilates(examPlanId);
    }

    @GetMapping("/addInvigilates")
    public int addInvigilates(@RequestParam("examPlanId") Integer examPlanId,
                              @RequestParam("classroomId") Integer classroomId,
                              @RequestParam("invigilatorIds") List<Long> invigilatorIds) {
        return userService.addInvigilates(examPlanId, classroomId, invigilatorIds);
    }

    @GetMapping("/deleteInvigilateTime")
    public int deleteInvigilateTime(@RequestParam("examPlanId") Integer examPlanId,
                                    @RequestParam("invigilateId") String invigilateId) {
        return userService.deleteInvigilateTime(examPlanId, invigilateId);
    }

    @GetMapping("/verifyInvigilate")
    public List<InvigilatorVO> verifyInvigilate(InvigilatorParameterReq ipo) {
        return userService.verifyInvigilate(ipo.getExamPlanId(), ipo.getClassroomId(), ipo.getInvigilateIds());
    }

    @GetMapping("/getStudentList")
    public PageResp<UserResp> getStudentList(@Validated PageQuery pageQuery,
                                             @Validated UserQuery query,
                                             @RequestParam("nickname") String nickname) {
        Long orgId = orgService.getOrgId(TokenLocalThreadUtil.get().getUserId());
        return userService.getStudentList(pageQuery, query, nickname, orgId);
    }

    @GetMapping("/getStudentDocumentTypeStatus")
    public List<StudentDocumentTypeVO> getStudentDocumentTypeStatus(@RequestParam("documentTypeList") List<String> documentTypeList) {
        return userService.getStudentDocumentTypeStatus(documentTypeList);
    }

}
