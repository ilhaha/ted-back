package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 持证信息详情信息
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "持证信息详情信息")
public class LicenseHolderInfoDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long examineeId;

    /**
     * 持证项目编码
     */
    @Schema(description = "持证项目编码")
    @ExcelProperty(value = "持证项目编码")
    private String projectCode;

    /**
     * 项目等级  0-无 1一级 2 二级
     */
    @Schema(description = "项目等级  0-无 1一级 2 二级")
    @ExcelProperty(value = "项目等级  0-无 1一级 2 二级")
    private Integer projectLevel;

    /**
     * 有效开始日期
     */
    @Schema(description = "有效开始日期")
    @ExcelProperty(value = "有效开始日期")
    private LocalDate validStartDate;

    /**
     * 有效结束日期
     */
    @Schema(description = "有效结束日期")
    @ExcelProperty(value = "有效结束日期")
    private LocalDate validEndDate;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Boolean isDeleted;
}