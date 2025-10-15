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

package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 项目信息
 *
 * @author Anton
 * @since 2025/03/11 15:14
 */
@Data
@Schema(description = "项目信息")
public class ProjectResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "所属八大类")
    private String categoryName;;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 项目代号
     */
    @Schema(description = "项目代号")
    private String projectCode;

    /**
     * 考试时长(分钟)
     */
    @Schema(description = "考试时长(分钟)")
    private Integer examDuration;

    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    private String deptName;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String redeme;

    //    /**
    //     * 更新人ID
    //     */
    //    @Schema(description = "更新人ID")
    //    private Long updateUser;

    /**
     * 更新时间戳
     */
    @Schema(description = "更新时间戳")
    private LocalDateTime updateTime;

    /**
     * 项目运行状态
     */
    @Schema(description = "项目运行状态")
    private Long projectStatus;

    /**
     * 展示图
     */
    @Schema(description = "展示图")
    private String imageUrl;

    /**
     * 项目类型（0-理论考试 1-实操考试）
     */
    @Schema(description = "项目类型")
    private Integer projectType;

    //    /**
    //     * 删除标记
    //     */
    //    @Schema(description = "删除标记")
    //    private Boolean isDeleted;

}