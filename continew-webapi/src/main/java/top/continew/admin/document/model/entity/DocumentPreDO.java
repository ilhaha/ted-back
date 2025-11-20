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

/**
 * 机构报考-考生上传资料实体
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@TableName("ted_document_pre")
public class DocumentPreDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构报考-考生扫码上传文件表id
     */
    private Long enrollPreUploadId;

    /**
     * 存储路径
     */
    private String docPath;

    /**
     * 关联资料类型ID
     */
    private Long typeId;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}