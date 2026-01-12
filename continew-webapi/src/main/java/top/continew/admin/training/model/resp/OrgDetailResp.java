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
import java.util.List;

/**
 * 机构信息详情信息
 *
 * @author Anton
 * @since 2025/04/07 10:53
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构信息详情信息")
public class OrgDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构代号
     */
    @Schema(description = "机构代号")
    @ExcelProperty(value = "机构代号")
    private String code;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String name;

    /**
     * 机构八大类归属
     */
    @Schema(description = "机构八大类归属")
    @ExcelProperty(value = "机构八大类归属")
    private String categoryNames;

    /**
     * 机构八大类归属
     */
    @Schema(description = "机构八大类归属")
    @ExcelProperty(value = "机构八大类归属")
    private List<Long> categoryIds;
    /**
     * 社会统一代码
     */
    @Schema(description = "社会统一代码")
    @ExcelProperty(value = "社会统一代码")
    private String socialCode;

    /**
     * 地点
     */
    @Schema(description = "地点")
    @ExcelProperty(value = "地点")
    private String location;

    /**
     * 法人
     */
    @Schema(description = "法人")
    @ExcelProperty(value = "法人")
    private String legalPerson;

    /**
     * 公司规模大小
     */
    @Schema(description = "公司规模大小")
    @ExcelProperty(value = "公司规模大小")
    private String scale;

    /**
     * 营业执照路径
     */
    @Schema(description = "营业执照路径")
    @ExcelProperty(value = "营业执照路径")
    private String businessLicense;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;

    /**
     * 考生名称
     */
    @Schema(description = "考生名称")
    @ExcelProperty(value = "考生名称")
    private List<String> candidateName;

    /**
     * 机构用户名
     */
    @Schema(description = "机构用户名")
    private String nickname;

    /**
     * 机构用户电话
     */
    //    @FieldEncrypt
    @Schema(description = "机构用户电话")
    private String phone;

    /**
     * 信誉分
     */
    @Schema(description = "信誉分")
    private Integer creditScore;

}