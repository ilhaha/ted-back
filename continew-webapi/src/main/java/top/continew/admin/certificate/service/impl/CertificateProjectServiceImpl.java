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

package top.continew.admin.certificate.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.certificate.mapper.CertificateProjectMapper;
import top.continew.admin.certificate.model.entity.CertificateProjectDO;
import top.continew.admin.certificate.model.query.CertificateProjectQuery;
import top.continew.admin.certificate.model.req.CertificateProjectReq;
import top.continew.admin.certificate.model.resp.CertificateProjectDetailResp;
import top.continew.admin.certificate.model.resp.CertificateProjectResp;
import top.continew.admin.certificate.service.CertificateProjectService;

/**
 * 证件项目关联业务实现
 *
 * @author Anton
 * @since 2025/03/17 09:56
 */
@Service
@RequiredArgsConstructor
public class CertificateProjectServiceImpl extends BaseServiceImpl<CertificateProjectMapper, CertificateProjectDO, CertificateProjectResp, CertificateProjectDetailResp, CertificateProjectQuery, CertificateProjectReq> implements CertificateProjectService {}