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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.NoArgsConstructor;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 专家信息实体
 *
 * @author Anton
 * @since 2025/04/07 10:45
 * 
 */

@Builder
@Data
@TableName("ted_expert")
@NoArgsConstructor // 无参构造
@AllArgsConstructor // 全参构造
public class ExpertDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专家名字
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 学历
     */
    private String education;

    /**
     * 专家称号
     */
    private String title;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;
}