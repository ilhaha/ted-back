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

package top.continew.admin.training.controller;

import com.alibaba.excel.EasyExcel;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.system.model.req.user.UserOrgDTO;
import top.continew.admin.training.listen.StudentDataListener;
import top.continew.admin.training.model.req.BindUserReq;
import top.continew.admin.training.model.req.OrgApplyPreReq;
import top.continew.admin.training.model.resp.OrgCandidatesResp;
import top.continew.admin.training.model.vo.AgencyStatusVO;
import top.continew.admin.training.model.vo.ProjectCategoryVO;
import top.continew.admin.training.model.vo.SelectOrgVO;
import top.continew.admin.training.model.vo.UserVO;
import top.continew.admin.util.Result;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.OrgQuery;
import top.continew.admin.training.model.req.OrgReq;
import top.continew.admin.training.model.resp.OrgDetailResp;
import top.continew.admin.training.model.resp.OrgResp;
import top.continew.admin.training.service.OrgService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 机构信息管理 API
 *
 * @author AntonF
 * @since 2025/04/07 10:53
 */
@Tag(name = "机构信息管理 API")
@RestController
@CrudRequestMapping(value = "/training/org", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class OrgController extends BaseController<OrgService, OrgResp, OrgDetailResp, OrgQuery, OrgReq> {
    @Resource
    private OrgService orgService;

    /**
     * 获取所有的机构作为选择器返回
     * @return
     */
    @GetMapping("/select")
    public List<SelectOrgVO> getOrgSelect() {
        return orgService.getOrgSelect();
    }
    /**
     * 机构预报名
     * @return
     */
    @PostMapping("/apply/pre")
    public Boolean applyPre(@Validated @RequestBody OrgApplyPreReq orgApplyPreReq) {
        return orgService.applyPre(orgApplyPreReq);
    }
    /**
     * 根据报考状态获取机构对应的项目-班级-考生级联选择 （预报名）
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/select/project/class/candidate")
    public List<ProjectCategoryVO> getSelectProjectClassCandidate(@RequestParam Long projectId) {
        return orgService.getSelectProjectClassCandidate(projectId);
    }

    /**
     * 获取机构对应的项目-班级级联选择
     * @return
     */
    @GetMapping("/select/project/class")
    public List<ProjectCategoryVO> getSelectProjectClass(@RequestParam Long orgId,@RequestParam Long projectId){
        return orgService.getSelectProjectClass(orgId,projectId);
    }

    /**
     * 获取机构对应的分类-项目-班级级联选择
     * @return
     */
    @GetMapping("/select/category/project/class")
    public List<ProjectCategoryVO> getSelectCategoryProjectClass(@RequestParam(required = false) Long orgId){
        return orgService.getSelectCategoryProjectClass(orgId);
    }

    /**
     * 获取机构对应的分类-项目级联选择
     * @return
     */
    @GetMapping("/select/category/project")
    public List<ProjectCategoryVO> getSelectCategoryProject(@RequestParam(required = false) Long orgId){
        return orgService.getSelectCategoryProject(orgId);
    }

    // ExcelController.java
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result uploadExcel(@RequestPart("file") MultipartFile file) {
        try {
            // 生成唯一文件ID（示例用UUID）
            String fileId = UUID.randomUUID().toString();

            // 存储到临时目录（示例路径：/temp/）
            String tempDir = System.getProperty("java.io.tmpdir");
            File targetFile = new File(tempDir, fileId + "_" + file.getOriginalFilename());
            file.transferTo(targetFile); // 保存文件

            return Result.success(fileId); // 返回文件ID给前端
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }

    @PostMapping("/parse")
    public Result parseExcel(@RequestParam String fileId) {
        try {
            // 根据文件ID查找临时文件（实现时需要匹配逻辑）
            String tempDir = System.getProperty("java.io.tmpdir");
            File[] files = new File(tempDir).listFiles((dir, name) -> name.startsWith(fileId + "_") // 根据ID前缀匹配文件名
            );

            if (files == null || files.length == 0) {
                return Result.error("文件不存在或已过期");
            }

            // 读取文件流
            InputStream inputStream = new FileInputStream(files[0]);

            // 解析Excel
            StudentDataListener listener = new StudentDataListener();
            EasyExcel.read(inputStream, UserOrgDTO.class, listener).sheet().doRead();

            // 后续处理（例如存储到Redis）
            List<UserOrgDTO> userOrgDTOS = orgService.processUserCredentials(listener.getData());

            orgService.batchSaveRedis(userOrgDTOS);

            // 删除临时文件（可选）
            //            files[0].delete();

            return Result.success(listener.getData());
        } catch (IOException e) {
            return Result.error("文件解析失败");
        }
    }

    @GetMapping("/getCandidates")
    public PageResp<OrgCandidatesResp> getCandidates(@Validated OrgQuery query,
                                                     @Validated PageQuery pageQuery,
                                                     @RequestParam(value = "type", required = false) String type) {
        return orgService.getCandidateList(query, pageQuery, type);
    }

    @GetMapping("/getAllOrgInfo/{orgStatus}")
    public PageResp<OrgResp> getAllOrgInfo(@Validated OrgQuery orgQuery,
                                           @Validated PageQuery pageQuery,
                                           @PathVariable(required = false) String orgStatus) {
        return orgService.getAllOrgInfo(orgQuery, pageQuery, orgStatus);
    }

    @GetMapping("/orgDetail/{orgId}")
    public OrgDetailResp orgDetail(@PathVariable Long orgId) {
        return orgService.getOrgDetail(orgId);
    }

    @GetMapping("/getAgencyStatus/{orgId}")
    public Result getAgencyStatus(@PathVariable Long orgId) {
        AgencyStatusVO statusVO = orgService.getAgencyStatus(orgId);
        return Result.success(statusVO);
    }

    @GetMapping("/studentAddAgency/{orgId}/{projectId}")
    public Integer studentAddAgency(@PathVariable Long orgId,@PathVariable Long projectId) {
        return orgService.studentAddAgency(orgId,projectId);
    }

    //学生退出机构
    @GetMapping("/studentQuitAgency/{orgId}")
    public Integer studentQuitAgency(@PathVariable Long orgId) {
        return orgService.studentQuitAgency(orgId);
    }

    // 机构移除学生
    @GetMapping("/agencyRemoveStudent/{orgId}/{candidateId}")
    public Integer agencyRemoveStudent(@PathVariable Long orgId,
                                       @PathVariable Long candidateId) {
        return orgService.agencyRemoveStudent(orgId, candidateId);
    }
    @GetMapping("/studentDelAgency/{orgId}")
    public Integer studentDelAgency(@PathVariable Long orgId) {
        return orgService.studentDelAgency(orgId);
    }

    @GetMapping("/approveStudent")
    public Integer approveStudent(@RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId) {
        return orgService.approveStudent(orgId, userId);
    }

    @GetMapping("/refuseStudent")
    public Integer refuseStudent(@RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId) {
        return orgService.refuseStudent(orgId, userId);
    }

    @GetMapping("/getOrgInfo")
    public OrgDetailResp getOrgInfo() {
        return orgService.getOrgInfo();
    }

    //获取机构账号列表
    @GetMapping("/accounts/{id}")
    public List<String> getOrgAccounts(@PathVariable String id) {
        return orgService.getOrgAccounts(id);
    }

    // 获取可绑定的用户列表
    @GetMapping("/bindable-users")
    public List<UserVO> getBindableUsers() {
        return orgService.getBindableUsers();
    }


    // 解绑机构用户
    @PostMapping("/unbind-user/{orgId}")
    public Boolean unbindUserToOrg(@PathVariable("orgId") Long orgId) {
        return orgService.unbindUserToOrg(orgId);
    }
    // 绑定用户到机构
    @PostMapping("/bind-user")
    public void bindUserToOrg(@RequestBody BindUserReq req) {
        orgService.bindUserToOrg(req.getOrgId(), req.getUserId());
    }

    // 删除机构及关联用户信息
    @DeleteMapping("/remove/{orgId}")
    public R<Void> removeOrgWithRelations(@PathVariable Long orgId) {
        orgService.removeOrgWithRelations(orgId);
        return R.ok("删除机构及关联信息成功");
    }


}