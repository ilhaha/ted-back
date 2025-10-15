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
import top.continew.admin.exam.mapper.SeatMapper;
import top.continew.admin.exam.model.entity.SeatDO;
import top.continew.admin.exam.model.query.SeatQuery;
import top.continew.admin.exam.model.req.SeatReq;
import top.continew.admin.exam.model.resp.SeatDetailResp;
import top.continew.admin.exam.model.resp.SeatResp;
import top.continew.admin.exam.service.SeatService;

/**
 * 座位表业务实现
 *
 * @author Anton
 * @since 2025/05/12 11:14
 */
@Service
@RequiredArgsConstructor
public class SeatServiceImpl extends BaseServiceImpl<SeatMapper, SeatDO, SeatResp, SeatDetailResp, SeatQuery, SeatReq> implements SeatService {}