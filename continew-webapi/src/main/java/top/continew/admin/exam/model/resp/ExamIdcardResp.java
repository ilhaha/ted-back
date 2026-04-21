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
 * 考生身份证信息信息
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Data
@Schema(description = "考生身份证信息信息")
public class ExamIdcardResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String realName;

    /**
     * 性别（男 女）
     */
    @Schema(description = "性别（男 女）")
    private String gender;

    /**
     * 民族
     */
    @Schema(description = "民族")
    private String nation;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    private LocalDate birthDate;

    /**
     * 住址
     */
    @Schema(description = "住址")
    private String address;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    private String idCardNumber;

    /**
     * 学历认证状态（0待审、1已认证、2认证未通过、3待认证）
     */
    @Schema(description = "学历认证状态（0待审、1已认证、2认证未通过、3待认证）")
    private Integer educationVerifyStatus;

    /**
     * 学信网学历验证报告
     */
    @Schema(description = "学信网学历验证报告")
    private String educationCertificate;

    /**
     * 提交学历认证时间
     */
    @Schema(description = "提交学历认证时间")
    private LocalDateTime educationVerifyTime;

    /**
     * 学历认证审核备注
     */
    @Schema(description = "学历认证审核备注")
    private String educationVerifyRemark;

    /**
     * 签发机关
     */
    @Schema(description = "签发机关")
    private String issuingAuthority;

    /**
     * 有效期开始日期
     */
    @Schema(description = "有效期开始日期")
    private LocalDate validStartDate;

    /**
     * 有效期截止日期
     */
    @Schema(description = "有效期截止日期")
    private LocalDate validEndDate;

    /**
     * 身份证正面照片路径
     */
    @Schema(description = "身份证正面照片路径")
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    @Schema(description = "身份证反面照片路径")
    private String idCardPhotoBack;

    /**
     * 专业类型
     */
    private String majorType;

    /**
     * 相关学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String graduatedSchool;

    /**
     * 相关专业
     */
    private String relatedMajor;

    /**
     * 单位名称
     */
    private String companyName;

    /**
     * 任职资格
     */
    private String qualification;

    /**
     * 相关工作年限
     */
    private Integer workYears;

    /**
     * 所属地区
     */
    private String region;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志：0否 1是
     */
    @Schema(description = "逻辑删除标志：0否 1是")
    private Integer isDeleted;
}