package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 焊接项目实操成绩详情信息
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "焊接项目实操成绩详情信息")
public class WeldingOperScoreDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @ExcelProperty(value = "考试计划ID")
    private Long planId;

    /**
     * 考试记录ID
     */
    @Schema(description = "考试记录ID")
    @ExcelProperty(value = "考试记录ID")
    private Long recordId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long candidateId;

    /**
     * 焊接项目代码
     */
    @Schema(description = "焊接项目代码")
    @ExcelProperty(value = "焊接项目代码")
    private String projectCode;

    /**
     * 实操成绩
     */
    @Schema(description = "实操成绩")
    @ExcelProperty(value = "实操成绩")
    private Integer operScore;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Boolean isDeleted;
}