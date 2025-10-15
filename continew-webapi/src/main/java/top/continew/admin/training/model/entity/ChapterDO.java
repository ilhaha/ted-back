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

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.util.List;

/**
 * 章节表实体
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@TableName("ted_chapter")
public class ChapterDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训ID
     */
    private Long trainingId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 父章节ID
     */
    private Long parentId;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 0-未删除 1-已删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private List<VideoDO> videos; // 当前章节关联的视频信息

    @TableField(exist = false)
    private List<ChapterDO> children; // 当前章节下面的子章节
}