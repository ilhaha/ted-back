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

package top.continew.admin.exam.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ProjLocAssocMapper;
import top.continew.admin.exam.model.entity.ProjLocAssocDO;
import top.continew.admin.exam.model.query.ProjLocAssocQuery;
import top.continew.admin.exam.model.req.ProjLocAssocReq;
import top.continew.admin.exam.model.resp.ProjLocAssocDetailResp;
import top.continew.admin.exam.model.resp.ProjLocAssocResp;
import top.continew.admin.exam.service.ProjLocAssocService;

/**
 * 项目地点关联业务实现
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Service
@RequiredArgsConstructor
public class ProjLocAssocServiceImpl extends BaseServiceImpl<ProjLocAssocMapper, ProjLocAssocDO, ProjLocAssocResp, ProjLocAssocDetailResp, ProjLocAssocQuery, ProjLocAssocReq> implements ProjLocAssocService {}