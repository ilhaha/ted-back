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

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 培训机构班级信息
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@Schema(description = "培训机构班级信息")
public class OrgClassResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @Schema(description = "机构id")
    private Long orgId;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private Long projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    private String className;

    /**
     * 作业人员扫码报考二维码
     */
    @Schema(description = "作业人员扫码报考二维码")
    private String qrcodeApplyUrl;

    /**
     * 班级类型，0作业人员班级，1检验人员班级
     */
    @Schema(description = "班级类型，0作业人员班级，1检验人员班级")
    private Integer classType;

    /**
     * 状态，0招生找，1停止招生
     */
    @Schema(description = "状态，0招生找，1停止招生")
    private Integer status;

    /**
     * 更新人id
     */
    @Schema(description = "更新人id")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @Schema(description = "是否删除 0-未删除 1-已删除")
    private Integer isDeleted;
}