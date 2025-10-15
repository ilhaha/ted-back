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

package top.continew.admin.expert.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ted_expert_free")
public class TedExpertFree implements Serializable {

    // 复合主键字段需全部标记为 @TableId（但 MyBatis-Plus 默认不允许多个 @TableId）
    // 若需兼容 MP 的通用方法（如 selectById），需自定义处理逻辑（见下方说明）

    @TableField("organization_id")
    private Long organizationId;

    @TableField("expert_id")
    private Long expertId;

    @TableField("project_id")
    private Long projectId;

    // 避免关键字冲突
    @TableField("free")
    private BigDecimal free;

    @TableField("pay_deadline_time")
    private LocalDateTime payDeadlineTime;
    @TableField("pay_completion_time")
    private LocalDateTime payCompletionTime;
    @TableField("status")
    private Long status;

}