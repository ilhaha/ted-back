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

package top.continew.admin.certificate.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.certificate.model.query.CertificateTypeQuery;
import top.continew.admin.certificate.model.req.CertificateTypeReq;
import top.continew.admin.certificate.model.resp.CertificateTypeDetailResp;
import top.continew.admin.certificate.model.resp.CertificateTypeResp;
import top.continew.admin.certificate.service.CertificateTypeService;

/**
 * 证件种类管理 API
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Tag(name = "证件种类管理 API")
@RestController
@CrudRequestMapping(value = "/certificate/certificateType", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE,
    Api.DELETE, Api.EXPORT})
public class CertificateTypeController extends BaseController<CertificateTypeService, CertificateTypeResp, CertificateTypeDetailResp, CertificateTypeQuery, CertificateTypeReq> {}