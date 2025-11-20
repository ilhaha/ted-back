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
 * 机构考生关联信息
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Data
@Schema(description = "机构考生关联信息")
public class OrgCandidateResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID
     */
    @Schema(description = "机构ID")
    private Long orgId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidateId;

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
     * 状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)
     */
    @Schema(description = "状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)")
    private Integer status;

    //    @FieldEncrypt
    @Schema(description = "考生手机号")
    private String phoneNumber;

    @Schema(description = "考生姓名")
    private String nickName;

    @Schema(description = "实名信息")
    private String idCardPhotos;

}