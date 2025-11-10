package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考生缴费审核实体
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@TableName("ted_examinee_payment_audit")
public class ExamineePaymentAuditDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联考试计划ID
     */
    private Long examPlanId;

    /**
     * 考生ID
     */
    private Long examineeId;

    /**
     * 关联报名记录ID
     */
    private Long enrollId;


    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    private String noticeNo;

    /**
     * 缴费金额（元）
     */
    private BigDecimal paymentAmount;

    /**
     * 缴费时间
     */
    private LocalDateTime paymentTime;

    /**
     * 缴费凭证URL
     */
    private String paymentProofUrl;

    /**
     * 缴费通知单URL
     */
    private String auditNoticeUrl;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回
     *
     */
    private Integer auditStatus;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 是否删除（0否，1是）
     */
    private Boolean isDeleted;

    /**
     * 作业人员班级id，如果是检验人员为空
     */
    private Long classId;

    /**
     * 上传通知单二维码URL，检验人员为空
     */
    private String qrcodeUploadUrl;
}