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

package top.continew.admin.exam.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 级联选择器节点
 * 父节点：考试计划
 * 子节点：准考证号
 *
 * @author ilhaha
 * @Create 2025/12/22 10:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CascaderOptionResp implements Serializable {

    /**
     * 节点值
     */
    private Object value;

    /**
     * 节点显示名称
     */
    private String label;

    /**
     * 子节点列表
     */
    private List<CascaderOptionResp> children;

    public CascaderOptionResp(Object value, String label) {
        this.value = value;
        this.label = label;
    }

}