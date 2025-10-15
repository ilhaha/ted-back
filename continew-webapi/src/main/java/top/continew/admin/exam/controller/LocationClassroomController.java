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

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.exam.model.query.LocationClassroomQuery;
import top.continew.admin.exam.model.req.LocationClassroomReq;
import top.continew.admin.exam.model.resp.LocationClassroomDetailResp;
import top.continew.admin.exam.model.resp.LocationClassroomResp;
import top.continew.admin.exam.service.LocationClassroomService;

/**
 * 考场地点和考场关联管理 API
 *
 * @author Anton
 * @since 2025/05/14 11:49
 */
@Tag(name = "考场地点和考场关联管理 API")
@RestController
@CrudRequestMapping(value = "/exam/locationClassroom", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class LocationClassroomController extends BaseController<LocationClassroomService, LocationClassroomResp, LocationClassroomDetailResp, LocationClassroomQuery, LocationClassroomReq> {}