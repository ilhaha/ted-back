package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生-考试项目考试状态详情信息
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生-考试项目考试状态详情信息")
public class CandidateExamProjectDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long candidateId;

    /**
     * 考试项目ID
     */
    @Schema(description = "考试项目ID")
    @ExcelProperty(value = "考试项目ID")
    private Long projectId;

    /**
     * 当前考试轮次（0表示未开始）
     */
    @Schema(description = "当前考试轮次（0表示未开始）")
    @ExcelProperty(value = "当前考试轮次（0表示未开始）")
    private Integer attemptNo;

    /**
     * 当前轮次是否已补考：0-否 1-是
     */
    @Schema(description = "当前轮次是否已补考：0-否 1-是")
    @ExcelProperty(value = "当前轮次是否已补考：0-否 1-是")
    private Boolean usedMakeup;

    /**
     * 是否通过：0-未通过 1-通过
     */
    @Schema(description = "是否通过：0-未通过 1-通过")
    @ExcelProperty(value = "是否通过：0-未通过 1-通过")
    private Boolean passed;

    /**
     * 通过时间
     */
    @Schema(description = "通过时间")
    @ExcelProperty(value = "通过时间")
    private LocalDateTime passTime;

    /**
     * 证书过期时间
     */
    @Schema(description = "证书过期时间")
    @ExcelProperty(value = "证书过期时间")
    private LocalDateTime certificateExpireTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Integer isDeleted;
}