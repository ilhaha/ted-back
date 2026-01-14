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

package top.continew.admin.common.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ilhaha
 * @Create 2025/11/4 19:21
 */
public class ImportWorkerTemplateConstant {

    /**
     * 导入作业人员模板表头（自定义）
     */
    public static final List<String> DEFAULT_HEAD = new ArrayList<>() {
        {
            add("姓名");
            add("联系方式");
            add("身份证人像面");
            add("身份证国徽面");
            add("一寸免冠照");
            add("报名资格申请表");
        }
    };

    /**
     * 导入作业人员模板表头（按照就系统）
     */
    public static final List<String> DEFAULT_HEAD_OLD = new ArrayList<>() {
        {
            add("姓名");
            add("身份证号");
            add("学历");
            add("联系电话");
            add("工作单位");
            add("工作区域");
            add("政治面貌");
        }
    };

    /**
     * 工作区域
     */
    public static final Set<String> BEIJING_DISTRICTS = Set
        .of("东城区", "西城区", "朝阳区", "丰台区", "石景山区", "海淀区", "顺义区", "通州区", "大兴区", "房山区", "门头沟区", "昌平区", "平谷区", "密云区", "怀柔区", "延庆区");

    /**
     * 学历
     */
    public static final Set<String> EDUCATION_SET = Set.of("小学", "初中", "高中", "专科", "本科", "研究生");

    /**
     * 政治面貌
     */
    public static final Set<String> POLITICAL_STATUS_SET = Set.of("中共党员", "共青团员", "群众");
}
