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

package top.continew.admin.training.listen;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.Getter;
import top.continew.admin.system.model.req.user.UserOrgDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Data

// StudentDataListener.java
public class StudentDataListener extends AnalysisEventListener<UserOrgDTO> {
    private List<UserOrgDTO> data = new ArrayList<>();

    // 每读一行触发
    @Override
    public void invoke(UserOrgDTO student, AnalysisContext context) {
        data.add(student);
    }

    // 读取完成触发
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("解析完成，共读取 " + data.size() + " 条数据");
    }

}
