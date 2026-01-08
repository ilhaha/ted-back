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

package top.continew.admin.document.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 资料类型主实体
 *
 * @author Anton
 * @since 2025/03/12 15:29
 */
@Data
@TableName("ted_document_type")
public class DocumentTypeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称(如身份证/学历证书)
     */
    private String typeName;


    /**
     * 必须上传人员（0全部都需要上传，1京籍上传、2非京籍上传）
     */
    private Integer needUploadPerson;

    /**
     * 更新人ID
     */
    private Long updateUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}