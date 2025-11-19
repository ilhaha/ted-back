package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生试卷详情信息
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生试卷详情信息")
public class CandidateExamPaperDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    @ExcelProperty(value = "报名id")
    private Long enrollId;

    /**
     * 试卷 JSON 内容
     */
    @Schema(description = "试卷 JSON 内容")
    @ExcelProperty(value = "试卷 JSON 内容")
    private String paperJson;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Integer isDeleted;
}