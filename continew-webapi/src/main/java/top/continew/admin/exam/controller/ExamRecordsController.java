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

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import top.continew.admin.exam.model.entity.ExamRecordsDO;
import top.continew.admin.exam.model.vo.CandidatesClassRoomVo;
import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.ExamRecordsQuery;
import top.continew.admin.exam.model.req.ExamRecordsReq;
import top.continew.admin.exam.model.resp.ExamRecordsDetailResp;
import top.continew.admin.exam.model.resp.ExamRecordsResp;
import top.continew.admin.exam.service.ExamRecordsService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 考试记录管理 API
 *
 * @author Anton
 * @since 2025/03/17 09:13
 */
@Tag(name = "考试记录管理 API")
@RestController
@CrudRequestMapping(value = "/exam/examRecords", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ExamRecordsController extends BaseController<ExamRecordsService, ExamRecordsResp, ExamRecordsDetailResp, ExamRecordsQuery, ExamRecordsReq> {

    @Resource
    private ExamRecordsService baseService;

    /**
     * 考生交卷
     * 
     * @param examRecordsDO
     * @return
     */
    @PostMapping("/candidates/add")
    public String candidatesAdd(@RequestBody ExamRecordsDO examRecordsDO) {
        baseService.candidatesAdd(examRecordsDO);
        return "添加成功";
    }

    /**
     * 根据身份证号获取考生所有的考场
     * 
     * @param username
     * @return
     */
    @SaIgnore
    @GetMapping("/by/idCard")
    public List<CandidatesClassRoomVo> getCandidatesClassRoom(@RequestParam("username") String username) {
        return baseService.getCandidatesClassRoom(username);
    }

    //重写page，加上考生名和项目名
    @GetMapping("/examRecords")
    public PageResp<ExamRecordsResp> examRecordsPage(@Validated ExamRecordsQuery query,
                                                     @Validated PageQuery pageQuery) {
        return baseService.examRecordsPage(query, pageQuery);
    }

    /**
     * 重写查询方法
     *
     * @param id
     * @return
     */
    @GetMapping("/getRecordsById/{id}")
    public ExamRecordsDetailResp getRecordsById(@PathVariable String id) {
        return baseService.getRecordsById(Long.valueOf(id));
    }

}