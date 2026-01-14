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
    //    private Long examPlanId;
    //
    //    @ExcelProperty("监考人员ID")
    //    private Long invigilatorId;

    /**
     * 导出字段
     */
    @ExcelProperty("监考人员")
    private String nickname;

    @ExcelProperty("考试计划")
    private String examPlanName;

    @ExcelProperty("考试日期")
    private String startTime;

    @ExcelProperty("考场名称")   // 新增考场名称
    private String classroomName;

    @ExcelProperty("实操考试劳务费（元）")
    private BigDecimal practicalFee;

    @ExcelProperty("理论考试劳务费（元）")
    private BigDecimal theoryFee;

    @ExcelProperty("劳务费合计（元）")
    private BigDecimal totalFee;

    //    @ExcelProperty("监考状态")
    //    private String invigilateStatus;

    @ExcelIgnore
    private Date createTime;
}
