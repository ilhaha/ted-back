package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试异步任务错误日志详情信息
 *
 * @author ilhaha
 * @since 2026/04/09 15:04
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试异步任务错误日志详情信息")
public class ExamAsyncErrorLogDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @ExcelProperty(value = "考试计划ID")
    private Long planId;

    /**
     * 报名ID
     */
    @Schema(description = "报名ID")
    @ExcelProperty(value = "报名ID")
    private Long enrollId;

    /**
     * 失败步骤
     */
    @Schema(description = "失败步骤")
    @ExcelProperty(value = "失败步骤")
    private String step;

    /**
     * 错误简要信息
     */
    @Schema(description = "错误简要信息")
    @ExcelProperty(value = "错误简要信息")
    private String errorMsg;

    /**
     * 异常堆栈信息
     */
    @Schema(description = "异常堆栈信息")
    @ExcelProperty(value = "异常堆栈信息")
    private String stackTrace;

    /**
     * 处理状态：0-未处理，1-已处理
     */
    @Schema(description = "处理状态：0-未处理，1-已处理")
    @ExcelProperty(value = "处理状态：0-未处理，1-已处理")
    private Integer status;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Integer isDeleted;
}