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

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ted_org_user")
public class TedOrgUser {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    @TableField("org_id")
    private Long orgId; // 机构ID

    @TableField("user_id")
    private Long userId; // 用户ID

    @TableField("create_user")
    private Long createUser; // 创建人ID

    @TableField("update_user")
    private Long updateUser; // 更新人ID

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted; // 是否删除（0-未删除，1-已删除）
}