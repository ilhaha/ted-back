package top.continew.admin.invigilate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考试劳务费配置详情信息
 *
 * @author ilhaha
 * @since 2025/12/11 14:51
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试劳务费配置详情信息")
public class LaborFeeDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 实操考试劳务费单价（元）
     */
    @Schema(description = "实操考试劳务费单价（元）")
    @ExcelProperty(value = "实操考试劳务费单价（元）")
    private BigDecimal practicalFee;

    /**
     * 理论考试劳务费单价（元）
     */
    @Schema(description = "理论考试劳务费单价（元）")
    @ExcelProperty(value = "理论考试劳务费单价（元）")
    private BigDecimal theoryFee;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 是否启用：1启用 0禁用
     */
    @Schema(description = "是否启用：1启用 0禁用")
    @ExcelProperty(value = "是否启用：1启用 0禁用")
    private Boolean isEnabled;
}