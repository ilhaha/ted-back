package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考试劳务费配置详情信息
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考试劳务费配置详情信息")
public class ExamViolationDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @ExcelProperty(value = "计划ID")
    private Long planId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long candidateId;

    /**
     * 违规描述
     */
    @Schema(description = "违规描述")
    @ExcelProperty(value = "违规描述")
    private String violationDesc;

    /**
     * 违规图片
     */
    @Schema(description = "违规图片")
    @ExcelProperty(value = "违规图片")
    private String illegalUrl;
}