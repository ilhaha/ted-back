package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考生缴费审核信息
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@Schema(description = "考生缴费审核信息")
public class ExamineePaymentAuditResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联考试计划ID
     */
    @Schema(description = "关联考试计划ID")
    private Long examPlanId;

    /**
     * 关联考试计划name
     */
    @Schema(description = "关联考试计划name")
    private String planName;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long examineeId;

    /**
     * 考生name
     */
    @Schema(description = "考生name")
    private String examineeName;

    /**
     * 关联报名记录ID
     */
    @Schema(description = "关联报名记录ID")
    private Long enrollId;

    /**
     * 缴费金额（元）
     */
    @Schema(description = "缴费金额（元）")
    private BigDecimal paymentAmount;

    /**
     * 缴费时间
     */
    @Schema(description = "缴费时间")
    private LocalDateTime paymentTime;

    /**
     * 缴费凭证URL
     */
    @Schema(description = "缴费凭证URL")
    private String paymentProofUrl;

    /**
     * 缴费通知单URL
     */
    @Schema(description = "缴费通知单URL")
    private String auditNoticeUrl;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回
     */
    @Schema(description = "0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回")
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    @Schema(description = "驳回原因")
    private String rejectReason;

    /**
     * 审核人ID
     */
    @Schema(description = "审核人ID")
    private Long auditorId;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0否，1是）
     */
    @Schema(description = "是否删除（0否，1是）")
    private Boolean isDeleted;
}