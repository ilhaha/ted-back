package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考生缴费审核详情信息
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生缴费审核详情信息")
public class ExamineePaymentAuditDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联考试计划ID
     */
    @Schema(description = "关联考试计划ID")
    @ExcelProperty(value = "关联考试计划ID")
    private Long examPlanId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long examineeId;

    /**
     * 关联报名记录ID
     */
    @Schema(description = "关联报名记录ID")
    @ExcelProperty(value = "关联报名记录ID")
    private Long enrollId;


    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号")
    @ExcelProperty(value = "缴费通知单编号")
    private String noticeNo;

    /**
     * 缴费金额（元）
     */
    @Schema(description = "缴费金额（元）")
    @ExcelProperty(value = "缴费金额（元）")
    private BigDecimal paymentAmount;

    /**
     * 缴费时间
     */
    @Schema(description = "缴费时间")
    @ExcelProperty(value = "缴费时间")
    private LocalDateTime paymentTime;

    /**
     * 缴费凭证URL
     */
    @Schema(description = "缴费凭证URL")
    @ExcelProperty(value = "缴费凭证URL")
    private String paymentProofUrl;

    /**
     * 缴费通知单URL
     */
    @Schema(description = "缴费通知单URL")
    @ExcelProperty(value = "缴费通知单URL")
    private String auditNoticeUrl;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核驳回
     */
    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核驳回")
    @ExcelProperty(value = "审核状态：0-待审核，1-审核通过，2-审核驳回")
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    @Schema(description = "驳回原因")
    @ExcelProperty(value = "驳回原因")
    private String rejectReason;

    /**
     * 审核人ID
     */
    @Schema(description = "审核人ID")
    @ExcelProperty(value = "审核人ID")
    private Long auditorId;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    @ExcelProperty(value = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 是否删除（0否，1是）
     */
    @Schema(description = "是否删除（0否，1是）")
    @ExcelProperty(value = "是否删除（0否，1是）")
    private Boolean isDeleted;
}