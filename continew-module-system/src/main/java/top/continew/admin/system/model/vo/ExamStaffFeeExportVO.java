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
