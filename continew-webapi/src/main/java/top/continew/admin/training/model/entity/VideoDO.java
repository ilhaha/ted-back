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

/**
 * 视频实体
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@TableName("ted_video")
public class VideoDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 视频路径
     */
    private String videoPath;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 0-未删除 1-已删除
     */
    private Integer isDeleted;

    //转成 hh:mm::dd
    @TableField(exist = false)
    private String durationStr;

    @TableField(exist = false)
    private String learningTime;

    @TableField(exist = false)
    private Double learningPercentage;

    private Long trainingId;

}