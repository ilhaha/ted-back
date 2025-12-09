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

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.req.AdjustPlanTimeReq;
import top.continew.admin.exam.model.req.ExamPlanSaveReq;
import top.continew.admin.exam.model.vo.OrgExamPlanVO;
import top.continew.admin.exam.model.vo.ProjectVo;
import top.continew.admin.exam.service.ProjectService;
import top.continew.admin.invigilate.model.resp.AvailableInvigilatorResp;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamPlanQuery;
import top.continew.admin.exam.model.req.ExamPlanReq;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
import top.continew.admin.exam.service.ExamPlanService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 考试计划管理 API
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Tag(name = "考试计划管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examPlan", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class ExamPlanController extends BaseController<ExamPlanService, ExamPlanResp, ExamPlanDetailResp, ExamPlanQuery, ExamPlanReq> {

    @Autowired
    private ProjectService projectService;

    @Resource
    private ExamPlanService examPlanService;


    /**
     * 调整考试/报名时间
     * @param req
     * @param planId
     * @return
     */
    @PostMapping("/adjustPlanTime/{planId}")
    public Boolean adjustPlanTime(@Validated @RequestBody AdjustPlanTimeReq req, @PathVariable("planId") Long planId ) {
        return baseService.adjustPlanTime(req,planId);
    }

    /**
     * 中心主任确认考试
     * 
     * @param planId
     * @param isFinalConfirmed
     * @return
     */
    @PostMapping("/conform/{planId}/{isFinalConfirmed}")
    public Boolean centerDirectorConform(@PathVariable("planId") Long planId,
                                         @PathVariable("isFinalConfirmed") Integer isFinalConfirmed) {
        return baseService.centerDirectorConform(planId, isFinalConfirmed);
    }

    /**
     * 获取可用监考员
     * 
     * @param planId
     * @return
     */
    @GetMapping("/available/invigilator/{planId}/{rejectedInvigilatorId}")
    public List<AvailableInvigilatorResp> getAvailableInvigilator(@PathVariable("planId") Long planId,
                                                                  @PathVariable("rejectedInvigilatorId") Long rejectedInvigilatorId) {
        return baseService.getAvailableInvigilator(planId, rejectedInvigilatorId);
    }

    /**
     * 重新随机分配考试计划的监考员
     * 
     * @param planId
     * @param invigilatorNum
     * @return
     */
    @PostMapping("/rest/random/invigilator/{planId}/{invigilatorNum}")
    public Boolean reRandomInvigilators(@PathVariable("planId") Long planId,
                                        @PathVariable("invigilatorNum") Integer invigilatorNum) {
        return examPlanService.reRandomInvigilators(planId, invigilatorNum);
    }

    /**
     * 机构获取符合自身八大类的考试计划
     *
     * @param examPlanQuery 考试计划查询参数
     * @param pageQuery     分页参数
     * @return 分页结果
     */
    @GetMapping({"/org/page"})
    public PageResp<OrgExamPlanVO> orgGetPlanList(ExamPlanQuery examPlanQuery, @Validated PageQuery pageQuery) {
        return examPlanService.orgGetPlanList(examPlanQuery, pageQuery);
    }

    @Operation(summary = "批量导入考试计划")
    @PostMapping("/import/excel")
    public Boolean importExcel(@RequestPart("file") MultipartFile file) {
        baseService.importExcel(file);
        return true;
    }

    @Operation(summary = "结束考试计划")
    @PostMapping("/end/{planId}")
    public Boolean endExam(@PathVariable("planId") Long planId) {
        return examPlanService.endExam(planId);
    }

    @Operation(summary = "获取考试计划考场")
    @GetMapping("/classroom/{planId}")
    public List<Long> getPlanExamClassroom(@PathVariable("planId") Long planId) {
        return examPlanService.getPlanExamClassroom(planId);
    }

    @Operation(summary = "修改考试计划考场")
    @PutMapping("/classroom/{planId}")
    public Boolean updatePlanExamClassroom(@PathVariable("planId") Long planId, @RequestBody List<Long> classroomId) {
        return examPlanService.updatePlanExamClassroom(planId, classroomId);
    }

    /**
     * 获取部门项目
     *
     * @return
     */
    @Operation(summary = "根据部门ID获取项目列表")
    @PostMapping("/dept/projectList/{planType}")
    public List<ProjectVo> getDeptProject(@PathVariable("planType") Integer planType) {
        return projectService.getDeptProject(planType);
    }

    /**
     * 发布考试计划
     *
     * @param examPlanSaveReq
     * @return
     */
    @PostMapping("/save")
    public String save(@RequestBody ExamPlanSaveReq examPlanSaveReq) {
        examPlanService.save(examPlanSaveReq);
        return "成功";
    }

    @Operation(summary = "考试计划审核")
    @PostMapping("/valid/{examPlanId}/{status}")
    public String valid(@PathVariable("examPlanId") Long id, @PathVariable("status") Integer status) {
        return examPlanService.valid(id, status);
    }

    @Operation(summary = "获取考试计划列表")
    @GetMapping("/list")
    public List<ExamPlanDO> list() {
        return examPlanService.getAllList();
    }

    /**
     * 获取下拉框
     */
    @Operation(summary = "获取考试记录下拉框")
    @GetMapping("/selectOptions")
    public List<ProjectVo> getSelectOptions() {
        return baseService.getSelectOptions();
    }

}