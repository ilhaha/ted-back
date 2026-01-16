package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构申请焊接考试项目详情信息
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构申请焊接考试项目详情信息")
public class WeldingExamApplicationDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请机构ID
     */
    @Schema(description = "申请机构ID")
    @ExcelProperty(value = "申请机构ID")
    private Long orgId;

    /**
     * 焊接类型：0-金属焊接，1-非金属焊接
     */
    @Schema(description = "焊接类型：0-金属焊接，1-非金属焊接")
    @ExcelProperty(value = "焊接类型：0-金属焊接，1-非金属焊接")
    private Integer weldingType;

    /**
     * 焊接考试项目名称
     */
    @Schema(description = "焊接考试项目名称")
    @ExcelProperty(value = "焊接考试项目名称")
    private String projectName;

    /**
     * 考试项目代码
     */
    @Schema(description = "考试项目代码")
    @ExcelProperty(value = "考试项目代码")
    private String projectCode;

    /**
     * 申请原因或说明
     */
    @Schema(description = "申请原因或说明")
    @ExcelProperty(value = "申请原因或说明")
    private String applicationReason;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核拒绝")
    @ExcelProperty(value = "审核状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;

    /**
     * 审核意见/备注
     */
    @Schema(description = "审核意见/备注")
    @ExcelProperty(value = "审核意见/备注")
    private String reviewComment;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间")
    @ExcelProperty(value = "提交时间")
    private LocalDateTime submittedAt;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    @ExcelProperty(value = "审核时间")
    private LocalDateTime reviewedAt;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    @ExcelProperty(value = "删除标记")
    private Boolean isDeleted;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    @ExcelProperty(value = "机构名称")
    private String orgName;
}