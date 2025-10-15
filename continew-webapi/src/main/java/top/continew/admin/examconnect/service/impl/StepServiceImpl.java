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

package top.continew.admin.examconnect.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.examconnect.mapper.StepMapper;
import top.continew.admin.examconnect.model.entity.StepDO;
import top.continew.admin.examconnect.model.query.StepQuery;
import top.continew.admin.examconnect.model.req.StepReq;
import top.continew.admin.examconnect.model.resp.StepDetailResp;
import top.continew.admin.examconnect.model.resp.StepResp;
import top.continew.admin.examconnect.service.StepService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 步骤，存储题目的不同回答步骤业务实现
 *
 * @author Anton
 * @since 2025/04/07 10:42
 */
@Service
@RequiredArgsConstructor
public class StepServiceImpl extends BaseServiceImpl<StepMapper, StepDO, StepResp, StepDetailResp, StepQuery, StepReq> implements StepService {

    @Override
    public List<StepResp> getListByQuestionId(Long questionId) {
        List<StepDO> stepDOS = baseMapper.selectList(new LambdaQueryWrapper<StepDO>()
            .eq(StepDO::getQuestionBankId, questionId));
        if (ObjectUtil.isEmpty(stepDOS)) {
            return Collections.emptyList();
        }
        return stepDOS.stream().map(item -> {
            StepResp stepResp = new StepResp();
            BeanUtils.copyProperties(item, stepResp);
            return stepResp;
        }).collect(Collectors.toList());
    }
}