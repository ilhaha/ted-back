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

package top.continew.admin.statemachine.invigilate;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.continew.admin.invigilate.model.enums.InvigilateStatus;

import java.util.EnumMap;
import java.util.Map;

@Component
public class StateRegistry implements ApplicationContextAware {
    private Map<InvigilateStatus, ExamPlanState> stateMap;

    public ExamPlanState getState(InvigilateStatus status) {
        ExamPlanState state = stateMap.get(status);
        if (state == null) {
            throw new IllegalArgumentException("无效状态: " + status);
        }
        return state;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext context) throws BeansException {
        stateMap = new EnumMap<>(InvigilateStatus.class);
        context.getBeansOfType(ExamPlanState.class)
            .values()
            .forEach(state -> stateMap.put(state.getStatusCode(), state));
    }
}