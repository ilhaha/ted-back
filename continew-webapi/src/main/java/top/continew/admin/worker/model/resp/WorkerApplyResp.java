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

package top.continew.admin.worker.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;
import java.util.Map;

/**
 * 作业人员报名信息
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@Schema(description = "作业人员报名信息")
public class WorkerApplyResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    private Long classId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    private String candidateName;

    /**
     * 作业人员性别
     */
    @Schema(description = "作业人员性别")
    private String gender;

    /**
     * 身份证住址
     */
    @Schema(description = "身份证住址")
    private String idCardAddress;

    /**
     * 作业人员手机号
     */
    @Schema(description = "作业人员手机号")
    //    @JsonMask(MaskType.MOBILE_PHONE)
    private String phone;

    /**
     * 报名资格申请表路径
     */
    @Schema(description = "报名资格申请表路径")
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    @Schema(description = "报名资格申请表名称")
    private String qualificationName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    //    @JsonMask(MaskType.ID_CARD)
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    @Schema(description = "身份证正面存储地址")
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    @Schema(description = "身份证反面存储地址")
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    @Schema(description = "一寸免冠照存储地址")
    private String facePhoto;

    /**
     * 审核状态:0待审核,1资料通过待考试,2未通过,3虚假材料,4待上传资料,5资料已上传,6资料待补全,7已考试
     */
    @Schema(description = "审核状态:0待审核,1资料通过待考试,2未通过,3虚假材料,4待上传资料,5资料已上传,6资料待补全,7已考试")
    private Integer status;

    /**
     * 报名方式，0作业人员自报名，1机构批量导入
     */
    @Schema(description = "报名方式，0作业人员自报名，1机构批量导入")
    private Integer applyType;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    private Integer isDeleted;

    /**
     * 八大类名称
     */
    @Schema(description = "八大类名称")
    private String categoryName;

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
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    private String remark;

    /**
     * 资料名称与资料路径映射
     */
    @Schema(description = "资料名称与资料路径映射")
    @ExcelProperty(value = "资料名称与资料路径映射")
    private Map<String, String> docMap;

    /**
     * 学历
     */
    private String education;

    /**
     * 工作单位
     */
    private String workUnit;

    /**
     * 通讯地址
     */
    private String address;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 焊接资格项目,非焊接项目可为空
     */
    @Schema(description = "焊接资格项目,非焊接项目可为空")
    private String weldingProjectCode;
}