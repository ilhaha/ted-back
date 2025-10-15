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

package top.continew.admin.exam.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.document.model.resp.DocumentTypeResp;
import top.continew.admin.exam.model.req.ExamLocationReqStr;
import top.continew.admin.exam.model.resp.StudentProjectDetailResp;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.exam.model.vo.ProjectWithClassroomVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ProjectQuery;
import top.continew.admin.exam.model.req.ProjectReq;
import top.continew.admin.exam.model.resp.ProjectDetailResp;
import top.continew.admin.exam.model.resp.ProjectResp;
import top.continew.admin.exam.service.ProjectService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 项目管理 API
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Tag(name = "项目管理 API")
@RestController
@CrudRequestMapping(value = "/exam/project", api = {Api.PAGE, Api.LIST, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ProjectController extends BaseController<ProjectService, ProjectResp, ProjectDetailResp, ProjectQuery, ProjectReq> {

    @Operation(summary = "查询所有有考场的考试项目")
    @GetMapping("/with-classrooms")
    public List<ProjectWithClassroomVO> getProjectsWithClassrooms() {
        return baseService.getProjectsWithClassrooms();
    }

    @Operation(summary = "根据项目ID插入地点")
    @PostMapping("/location/{projectId}")
    public String insertLocation(@PathVariable Long projectId, @RequestBody List<Long> locationList) {
        return baseService.insertLocation(projectId, locationList);
    }

    @Operation(summary = "根据项目ID插入资料")
    @PostMapping("/document/{projectId}")
    public String insertDocument(@PathVariable Long projectId, @RequestBody List<Long> documentList) {
        return baseService.insertDocument(projectId, documentList);
    }

    @Operation(summary = "根据项目ID查询地点")
    @GetMapping("/location/{projectId}")
    public List<ExamLocationReqStr> getLocation(@PathVariable Long projectId) {
        return baseService.getLocation(projectId);
    }

    @Operation(summary = "根据项目ID查询已绑定资料列表")
    @GetMapping("/document/{projectId}")
    public List<DocumentTypeResp> getDocument(@PathVariable Long projectId) {
        return baseService.getDocument(projectId);
    }

    //        @Operation(summary = "绑定 根据项目ids查询地点")
    @Operation(summary = "根据projectid获取没有关联的localtion")
    @PostMapping("/location/notBinding/{id}")
    public List<ProjectVo> getNotBindLocation(@PathVariable("id") Long id) {
        return baseService.notBindLocation(id);
    }

    @Operation(summary = "根据projectid获取没有关联的document")
    @PostMapping("/document/notBinding/{id}")
    public List<ProjectVo> getNotBindDocument(@PathVariable("id") Long id) {
        return baseService.notBindDocument(id);
    }

    @Operation(summary = "根据projectid获取关联的localtion")
    @PostMapping("/location/binding/{id}")
    public List<ProjectVo> getBindLocation(@PathVariable("id") Long id) {
        return baseService.getBindLocation(id);
    }

    @Operation(summary = "根据项目ID查询地点_下拉框")
    @GetMapping("/location/select/{projectId}")
    public List<ProjectVo> getLocationSelect(@PathVariable Long projectId) {
        return baseService.getLocationSelect(projectId);
    }

    @Operation(summary = "根据地点ID查询考场_下拉框")
    @GetMapping("/classroom/select/{locationId}/{examType}")
    public List<ProjectVo> getClassRoomSelect(@PathVariable Long locationId,@PathVariable Integer examType) {
        return baseService.getClassRoomSelect(locationId,examType);
    }

    @Operation(summary = "获取项目下拉框")
    @GetMapping("/selectOptions")
    public List<ProjectVo> selectOptions() {
        return baseService.selectOptions();
    }

    /*重写project删除方法
     *不仅删除项目表
     * 还删除project与localation关联表的关联关系
     * */
    @Override
    public void delete(List<Long> ids) {
        baseService.delete(ids);
    }

    @Operation(summary = "项目解绑地址")
    @DeleteMapping("/location/{projectId}")
    public void projectDelLocation(@RequestBody List<Long> locationIds, @PathVariable("projectId") Long projectId) {
        baseService.projectDelLocation(projectId, locationIds);
    }

    @Operation(summary = "项目解绑资料")
    @DeleteMapping("/document/{projectId}")
    public void projectDelDocument(@RequestBody List<Long> documentIds, @PathVariable("projectId") Long projectId) {
        baseService.projectDelDocument(projectId, documentIds);
    }

    /**
     * 考生段获取全部项目
     *
     * @return
     */
    @Operation(summary = "查询全部项目")
    @GetMapping("/getAllProject")
    public PageResp<ProjectResp> getAllProject(@Validated ProjectQuery query, @Validated PageQuery pageQuery) {
        return baseService.getAllProject(query, pageQuery);
    }

    @Operation(summary = "根据状态查询项目")
    @GetMapping("/getProjectByStatus/{projectStatus}")
    public PageResp<ProjectResp> getProjectByStatus(@Validated ProjectQuery query,
                                                    @Validated PageQuery pageQuery,
                                                    @PathVariable("projectStatus") Long projectStatus) {
        return baseService.getProjectByStatus(query, pageQuery, projectStatus);
    }

    @Operation(summary = "审核项目")
    @SaCheckPermission("exam:project:examine")
    @PutMapping("/examine/{projectId}")
    public void examine(@RequestBody ProjectReq projectReq, @PathVariable("projectId") Long projectId) {
        baseService.examine(projectReq, projectId);
    }

    @Operation(summary = "根据项目id查询学生项目详情")
    @GetMapping("/studentDetail/{projectId}")
    public StudentProjectDetailResp getStudentDetail(@PathVariable("projectId") Long projectId) {
        return baseService.getStudentProjectDetail(projectId);
    }

}