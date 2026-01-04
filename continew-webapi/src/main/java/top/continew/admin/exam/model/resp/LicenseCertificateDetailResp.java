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

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 人员及许可证书信息详情信息
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "人员及许可证书信息详情信息")
public class LicenseCertificateDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试记录id
     */
    @Schema(description = "考试记录id")
    @ExcelProperty(value = "考试记录id")
    private Long recordId;

    /**
     * 数据来源
     */
    @Schema(description = "数据来源")
    @ExcelProperty(value = "数据来源")
    private String datasource;

    /**
     * 信息录入单位
     */
    @Schema(description = "信息录入单位")
    @ExcelProperty(value = "信息录入单位")
    private String infoinputorg;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @ExcelProperty(value = "姓名")
    private String psnName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @ExcelProperty(value = "身份证号")
    private String idcardNo;

    /**
     * 原单位名称
     */
    @Schema(description = "原单位名称")
    @ExcelProperty(value = "原单位名称")
    private String originalComName;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称")
    @ExcelProperty(value = "单位名称")
    private String comName;

    /**
     * 申请类型
     */
    @Schema(description = "申请类型")
    @ExcelProperty(value = "申请类型")
    private Integer applyType;

    /**
     * 申请日期
     */
    @Schema(description = "申请日期")
    @ExcelProperty(value = "申请日期")
    private LocalDate applyDate;

    /**
     * 是否审核
     */
    @Schema(description = "是否审核")
    @ExcelProperty(value = "是否审核")
    private Integer isVerify;

    /**
     * 是否操作
     */
    @Schema(description = "是否操作")
    @ExcelProperty(value = "是否操作")
    private Integer isOpr;

    /**
     * 证书类别
     */
    @Schema(description = "证书类别")
    @ExcelProperty(value = "证书类别")
    private String lcnsKind;

    /**
     * 证书分类
     */
    @Schema(description = "证书分类")
    @ExcelProperty(value = "证书分类")
    private String lcnsCategory;

    /**
     * 证书编号
     */
    @Schema(description = "证书编号")
    @ExcelProperty(value = "证书编号")
    private String lcnsNo;

    /**
     * 证书签发日期
     */
    @Schema(description = "证书签发日期")
    @ExcelProperty(value = "证书签发日期")
    private LocalDate certDate;

    /**
     * 授权日期
     */
    @Schema(description = "授权日期")
    @ExcelProperty(value = "授权日期")
    private LocalDate authDate;

    /**
     * 证书有效期
     */
    @Schema(description = "证书有效期")
    @ExcelProperty(value = "证书有效期")
    private LocalDate endDate;

    /**
     * 原授权单位
     */
    @Schema(description = "原授权单位")
    @ExcelProperty(value = "原授权单位")
    private String originalAuthCom;

    /**
     * 授权单位
     */
    @Schema(description = "授权单位")
    @ExcelProperty(value = "授权单位")
    private String authCom;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态")
    private Integer state;

    /**
     * 证书项目名称
     */
    @Schema(description = "证书项目名称")
    @ExcelProperty(value = "证书项目名称")
    private String psnlcnsItem;

    /**
     * 证书项目代码
     */
    @Schema(description = "证书项目代码")
    @ExcelProperty(value = "证书项目代码")
    private String psnlcnsItemCode;

    /**
     * 许可范围
     */
    @Schema(description = "许可范围")
    @ExcelProperty(value = "许可范围")
    private String permitScope;

    /**
     * 明细备注
     */
    @Schema(description = "明细备注")
    @ExcelProperty(value = "明细备注")
    private String detailRemark;

    /**
     * 明细状态
     */
    @Schema(description = "明细状态")
    @ExcelProperty(value = "明细状态")
    private Integer detailState;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    @ExcelProperty(value = "是否删除（0-未删除，1-已删除）")
    private Integer isDeleted;
}