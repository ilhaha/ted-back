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

package top.continew.admin.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.common.enums.GenderEnum;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户详细信息 DTO
 *
 */
@Data
@Schema(description = "用户详细信息 DTO")
public class UserDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "用户ID", example = "713046832414036147")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    @Length(max = 255, message = "用户名长度不能超过255个字符")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "系统管理员")
    @Length(max = 30, message = "昵称长度不能超过30个字符")
    private String nickname;

    /**
     * 密码（仅新增/修改密码时传值）
     */
    @Schema(description = "密码（仅新增/修改密码时传值）", example = "123456")
    @Length(min = 6, max = 255, message = "密码长度需在6-255个字符之间")
    private String password;

    /**
     * 性别
     */
    @Schema(description = "性别（0：未知；1：男；2：女）", example = "1")
    private GenderEnum gender;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "admin@example.com")
    @Length(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称", example = "XX测试中心")
    @Length(max = 255, message = "单位名称长度不能超过255个字符")
    private String companyName;

    /**
     * 联系地址
     */
    @Schema(description = "联系地址", example = "广东省深圳市南山区XX街道XX大厦")
    @Length(max = 500, message = "联系地址长度不能超过500个字符")
    private String contactAddress;

    /**
     * 固定电话
     */
    @Schema(description = "固定电话", example = "0755-12345678")
    private String landline;

    /**
     * 所属地区
     */
    @Schema(description = "所属地区", example = "广东省-深圳市-南山区")
    @Length(max = 100, message = "所属地区长度不能超过100个字符")
    private String region;

    /**
     * 邮政编码
     */
    @Schema(description = "邮政编码", example = "518000")
    @Pattern(regexp = "^\\d{6}$", message = "邮政编码格式不正确（6位数字）")
    private String postalCode;

    /**
     * 毕业院校
     */
    @Schema(description = "毕业院校", example = "XX大学")
    @Length(max = 255, message = "毕业院校长度不能超过255个字符")
    private String graduationSchool;

    /**
     * 相关专业
     */
    @Schema(description = "相关专业", example = "机械工程")
    @Length(max = 100, message = "相关专业长度不能超过100个字符")
    private String relatedMajor;

    /**
     * 专业类型
     */
    @Schema(description = "专业类型", example = "工科")
    @Length(max = 50, message = "专业类型长度不能超过50个字符")
    private String majorType;

    /**
     * 相关学历
     */
    @Schema(description = "相关学历", example = "本科")
    @Length(max = 50, message = "相关学历长度不能超过50个字符")
    private String relatedEducation;

    /**
     * 任职资格
     */
    @Schema(description = "任职资格", example = "高级工程师")
    @Length(max = 100, message = "任职资格长度不能超过100个字符")
    private String jobQualification;

    /**
     * 相关工作年限（年）
     */
    @Schema(description = "相关工作年限（年）", example = "10")
    @Min(value = 0, message = "工作年限不能为负数")
    @Max(value = 255, message = "工作年限不能超过255年")
    private Integer relatedWorkYears;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatar;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "系统初始用户")
    @Length(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态（1：启用；2：禁用）", example = "1")
    private DisEnableStatusEnum status;

    /**
     * 是否为系统内置数据
     */
    @Schema(description = "是否为系统内置数据", example = "false")
    private Boolean isSystem;

    /**
     * 最后一次修改密码时间
     */
    @Schema(description = "最后一次修改密码时间", example = "2025-03-11 14:58:00")
    private LocalDateTime pwdResetTime;

    /**
     * 部门 ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-03-11 14:58:00")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2025-11-21 09:05:48")
    private LocalDateTime updateTime;
}