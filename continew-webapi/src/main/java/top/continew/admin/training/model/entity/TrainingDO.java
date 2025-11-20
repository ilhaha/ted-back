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
 * 培训主表实体
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@TableName("ted_training")
@Builder
@NoArgsConstructor // 无参构造
@AllArgsConstructor // 全参构造
public class TrainingDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训名称
     */
    private String title;

    /**
     * 封面路径
     */
    private String coverPath;

    /**
     * 视频总时长（秒）
     */
    private Integer totalDuration;

    /**
     * 专家ID
     */
    private Long expertId;

    //    /**
    //     * 专家名称
    //     */
    //    private String expertName;

    //    /**
    //     * 费用
    //     */
    //    private BigDecimal fee;

    /**
     * 培训描述
     */
    private String description;

    /**
     * 0- 待审核 1-审核通过 2-审核不通过
     */
    private Long status;

    /**
     * 0-未删除 1-已删除
     */
    private Integer isDeleted;
}