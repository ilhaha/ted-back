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

package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 机构信息实体
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
@Data
@TableName("ted_org")
public class OrgDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构代号
     */
    private String code;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 社会统一代码
     */
    private String socialCode;

    /**
     * 地点
     */
    private String location;

    /**
     * 法人
     */
    private String legalPerson;

    /**
     * 公司规模大小
     */
    private String scale;

    /**
     * 营业执照路径
     */
    private String businessLicense;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;
}