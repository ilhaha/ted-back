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
 * 创建或修改考生身份证信息参数
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Data
@Schema(description = "创建或修改考生身份证信息参数")
public class ExamIdcardReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Length(max = 64, message = "姓名长度不能超过 {max} 个字符")
    private String realName;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    @NotBlank(message = "身份证号码不能为空")
    @Length(max = 18, message = "身份证号码长度不能超过 {max} 个字符")
    private String idCardNumber;

    /**
     * 性别（男 女）
     */
    private String gender;

    /**
     * 民族
     */
    private String nation;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 住址
     */
    @Schema(description = "联系地址")
    @NotBlank(message = "联系地址不能为空")
    @Length(max = 255, message = "联系地址长度不能超过 {max} 个字符")
    private String address;

    /**
     * 身份证正面照片路径
     */
    @Schema(description = "身份证正面照片路径")
    @NotBlank(message = "未上传身份证正面照片")
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    @Schema(description = "身份证反面照片路径")
    @NotBlank(message = "未上传身份证反面照片")
    private String idCardPhotoBack;

    /**
     * 人像面照片路径
     */
    @Schema(description = "人脸证件照")
    @NotBlank(message = "未上传2寸人脸证件照")
    private String facePhoto;

    /**
     * 专业类型
     */
    @Schema(description = "专业类型")
    @NotBlank(message = "专业类型不能为空")
    private String majorType;

    /**
     * 相关学历
     */
    @Schema(description = "相关学历")
    @NotBlank(message = "相关学历不能为空")
    private String education;

    /**
     * 毕业院校
     */
    @Schema(description = "毕业院校")
    @NotBlank(message = "毕业院校不能为空")
    @Length(max = 128, message = "毕业院校长度不能超过 {max} 个字符")
    private String graduatedSchool;

    /**
     * 相关专业
     */
    @Schema(description = "相关专业")
    @NotBlank(message = "相关专业不能为空")
    @Length(max = 128, message = "相关专业长度不能超过 {max} 个字符")
    private String relatedMajor;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称")
    @NotBlank(message = "单位名称不能为空")
    @Length(max = 128, message = "单位名称长度不能超过 {max} 个字符")
    private String companyName;

    /**
     * 任职资格
     */
    @Schema(description = "任职资格")
    @NotBlank(message = "任职资格不能为空")
    private String qualification;

    /**
     * 相关工作年限
     */
    @Schema(description = "相关工作年限")
    @NotNull(message = "相关工作年限不能为空")
    @Min(value = 0, message = "工作年限不能小于 {value}")
    @Max(value = 50, message = "工作年限不能超过 {value}")
    private Integer workYears;

    /**
     * 所属地区
     */
    @Schema(description = "所属地区")
    @NotBlank(message = "所属地区不能为空")
    private String region;

    /**
     * 邮政编码
     */
    @Schema(description = "邮政编码")
    @NotBlank(message = "邮政编码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "邮政编码必须为6位数字")
    private String postalCode;

    /**
     * 电子邮箱
     */
    @Schema(description = "电子邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

}