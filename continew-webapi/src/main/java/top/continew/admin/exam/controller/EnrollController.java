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

import jakarta.annotation.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.exam.model.resp.*;
import top.continew.admin.exam.model.vo.ApplyListVO;
import top.continew.admin.exam.model.vo.ExamCandidateVO;
import top.continew.admin.exam.model.vo.IdentityCardExamInfoVO;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.EnrollQuery;
import top.continew.admin.exam.model.req.EnrollReq;
import top.continew.admin.exam.service.EnrollService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 考生报名表管理 API
 *
 * @author zmk
 * @since 2025/03/24 14:04
 */
@Tag(name = "考生报名表管理 API")
@RestController
@CrudRequestMapping(value = "/exam/enroll", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class EnrollController extends BaseController<EnrollService, EnrollResp, EnrollDetailResp, EnrollQuery, EnrollReq> {
    @Resource
    private EnrollService enrollService;

    /**
     * 下载某个班级的考试缴费通知单
     * @param classId
     * @param planId
     * @return
     */
    @GetMapping("/download/batch/auditNotice/{classId}/{planId}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable("classId") Long classId, @PathVariable("planId") Long planId) {
        return enrollService.downloadBatchAuditNotice(classId,planId);
    }

    /**
     * 下载某个考试的缴费通知单
     * @param enrollId
     * @return
     */
    @GetMapping("/download/auditNotice/{enrollId}")
    public ResponseEntity<byte[]> downloadAuditNotice(@PathVariable("enrollId") Long enrollId) {
        return enrollService.downloadAuditNotice(enrollId);
    }

    //    /**
    //     * 考生报名
    //     * @param enrollReq
    //     * @return
    //     */
    //    @PostMapping("/singUp")
    //    public Boolean singUp(@Validated @RequestBody EnrollReq enrollReq){
    //        return enrollService.singUp(enrollReq);
    //    }

    /**
     * 获取考试计划对应考场的考生列表
     * 
     * @param enrollQuery
     * @param pageQuery
     * @param planId
     * @param classroomId
     * @return
     */
    @GetMapping("/candidates/{planId}/{classroomId}")
    public PageResp<ExamCandidateVO> getExamCandidates(EnrollQuery enrollQuery,
                                                       @Validated PageQuery pageQuery,
                                                       @PathVariable("planId") Long planId,
                                                       @PathVariable("classroomId") Long classroomId) {
        return enrollService.getExamCandidates(enrollQuery, pageQuery, planId, classroomId);
    }

    /**
     * 获取对应考试计划的详细信息
     * 
     * @param examPlanId
     * @return ;
     */
    ;

    @GetMapping("/getAllDetail/byProject/{examPlanId}")
    public EnrollDetailResp getAllDetailEnrollList(@PathVariable("examPlanId") Long examPlanId) {
        return enrollService.getAllDetailEnrollList(examPlanId);
    }

    /**
     * 获取对应状态的考生报名计划详细
     * 
     * @param examPlanId;
     * @return EnrollStatusDetailResp
     */
    @GetMapping("/getStatusDetail/byProject/{examPlanId}")
    public EnrollStatusDetailResp getEnrollStatusDetail(@PathVariable("examPlanId") Long examPlanId) {
        return enrollService.getEnrollStatusDetail(examPlanId);
    }

    /**
     * 分页获取对应状态的考生报名
     * 
     * @param query        查询条件
     * @param pageQuery    分页参数
     * @param enrollStatus 报名状态（可选）
     * @return 分页结果
     */
    @GetMapping("/getEnrollStatusList")
    public PageResp<EnrollStatusResp> getEnrollStatusList(
            @Validated EnrollQuery query,
            @Validated PageQuery pageQuery,
            @RequestParam(required = false) Long enrollStatus
    ) {
        return enrollService.getEnrollStatusList(query, pageQuery, enrollStatus);
    }


    @GetMapping("/getEnrollInfo")
    public EnrollInfoResp getEnrollInfo() {
        return enrollService.getEnrollInfo();
    }

    @PostMapping("/getScore")
    public Map<String, String> getScore(@RequestBody Map<Object, Object> map) {
        return enrollService.getScore(map.get("username").toString(), map.get("identity").toString());
    }

    @GetMapping("/checkEnrollTime")
    public void checkEnrollTime(@RequestParam Long examPlanId) {
        enrollService.checkEnrollTime(examPlanId);
    }

    @GetMapping("/viewIdentityCard/{examPlanId}")
    public IdentityCardExamInfoVO viewIdentityCard(@PathVariable("examPlanId") Long examPlanId) {
        return enrollService.viewIdentityCard(examPlanId);
    }

    @GetMapping("/cancelEnroll/{examPlanId}")
    public void cancelEnroll(@PathVariable("examPlanId") Long examPlanId) {
        enrollService.cancelEnroll(examPlanId);
    }

}