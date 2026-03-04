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

package top.continew.admin.system.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class ExamStaffFeeExportVO {
    //
    //    @ExcelProperty("考试计划ID")
    @ExcelIgnore
    private Long examPlanId;
    //
    //    @ExcelProperty("监考人员ID")
    @ExcelIgnore
    private Long invigilatorId;

    /** 监考员身份证号 */
    @ExcelIgnore
    private String invigilatorIdNumber;

    /** 监考员开户银行名称 */
    @ExcelIgnore
    private String invigilatorBankName;

    /** 监考员银行卡号 */
    @ExcelIgnore
    private String invigilatorBankAccount;

    /** 监考员所属单位 */
    @ExcelIgnore
    private String invigilatorUnit;

    /** 监考员职称 */
    @ExcelIgnore
    private String invigilatorTitle;
    /**
     * 联系方式
     */
    private String phone;

    /**
     * 监考种类名称
     */
    @ExcelIgnore
    private String categoryName;

    /**
     * 监考项目
     */
    @ExcelIgnore
    private String projectName;

    /**
     * 导出字段
     */
    @ExcelProperty("监考人员")
    private String nickname;

    @ExcelProperty("考试计划")
    private String examPlanName;

    @ExcelProperty("考试日期")
    private String startTime;

    @ExcelProperty("考场名称")
    private String classroomName;
    @ExcelProperty("考试类型")
    private String examTypeName;

    @ExcelProperty("监考劳务费")
    private BigDecimal examFee;

    //    @ExcelProperty("实发金额")
    @ExcelIgnore
    private String totalFee;

    //    @ExcelProperty("监考状态")
    //    private String invigilateStatus;

    @ExcelIgnore
    private Date createTime;
}
