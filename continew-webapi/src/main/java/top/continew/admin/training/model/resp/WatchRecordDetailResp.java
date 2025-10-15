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

package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 学习记录详情信息
 *
 * @author Anton
 * @since 2025/03/24 15:35
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "学习记录详情信息")
public class WatchRecordDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @ExcelProperty(value = "学生ID")
    private Long studentId;

    @Schema(description = "学生名称")
    @ExcelProperty(value = "学生名称")
    private String studentName;

    /**
     * 视频ID
     */
    @Schema(description = "视频ID")
    @ExcelProperty(value = "视频ID")
    private Long videoId;

    @Schema(description = "视频名称")
    @ExcelProperty(value = "视频名称")
    private String videoName;

    /**
     * 已观看时长（秒）
     */
    @Schema(description = "已观看时长（秒）")
    @ExcelProperty(value = "已观看时长（秒）")
    private Integer watchedDuration;

    /**
     * 0-未观看 1-已观看 2-已完成
     */
    @Schema(description = "0-未观看 1-已观看 2-已完成")
    @ExcelProperty(value = "0-未观看 1-已观看 2-已完成")
    private Integer status;

    /**
     * 0-未删除 1-已删除
     */
    @Schema(description = "0-未删除 1-已删除")
    @ExcelProperty(value = "0-未删除 1-已删除")
    private Integer isDeleted;
}