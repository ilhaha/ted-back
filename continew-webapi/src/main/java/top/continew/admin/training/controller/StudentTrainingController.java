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

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.training.model.query.StudentTrainingQuery;
import top.continew.admin.training.model.req.StudentTrainingReq;
import top.continew.admin.training.model.resp.StudentTrainingDetailResp;
import top.continew.admin.training.model.resp.StudentTrainingResp;
import top.continew.admin.training.service.StudentTrainingService;

/**
 * 学生培训管理 API
 *
 * @author Anton
 * @since 2025/03/26 11:52
 */
@Tag(name = "学生培训管理 API")
@RestController
@CrudRequestMapping(value = "/training/studentTraining", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class StudentTrainingController extends BaseController<StudentTrainingService, StudentTrainingResp, StudentTrainingDetailResp, StudentTrainingQuery, StudentTrainingReq> {}