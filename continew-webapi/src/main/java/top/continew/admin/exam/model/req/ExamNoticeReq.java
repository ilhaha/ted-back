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

package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改无损检测、检验人员考试通知参数
 *
 * @author ilhaha
 * @since 2026/04/14 15:20
 */
@Data
@Schema(description = "创建或修改无损检测、检验人员考试通知参数")
public class ExamNoticeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Length(max = 1000, message = "标题长度不能超过 {max} 个字符")
    private String title;

    /**
     * 报名截止时间
     */
    @Schema(description = "报名截止时间")
    @NotNull(message = "报名截止时间不能为空")
    private LocalDate applyDeadline;

    /**
     * 所属类别
     */
    @Schema(description = "所属类别")
    @NotNull(message = "所属类别不能为空")
    private Long categoryId;

    /**
     * 考试等级 0-无 1一级 2 二级
     */
    @Schema(description = "考试等级  0-无 1一级 2 二级")
    @NotNull(message = "考试等级  0-无 1一级 2 二级不能为空")
    private Integer examLevel;

    /**
     * 状态（0待审核、1审核通过、2审核未通过）
     */
    @Schema(description = "状态（0待审核、1审核通过、2审核未通过）")
    @NotNull(message = "状态（0待审核、1审核通过、2审核未通过）不能为空")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 1000, message = "备注长度不能超过 {max} 个字符")
    private String remark;
}