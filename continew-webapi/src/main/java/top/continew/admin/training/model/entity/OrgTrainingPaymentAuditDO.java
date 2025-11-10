package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）实体
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Data
@TableName("ted_org_training_payment_audit")
public class OrgTrainingPaymentAuditDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联机构ID（关联机构表主键）
     */
    private Long orgId;

    /**
     * 关联培训ID（关联机构培训价格表主键）
     */
    private Long trainingId;

    /**
     * 关联八大类ID（关联八大类字典表主键）
     */
    private Long categoryId;

    /**
     * 考生ID（缴费考生，关联用户表主键）
     */
    private Long candidateId;

    /**
     * 关联报名记录ID（关联机构培训报名记录表主键）
     */
    private Long enrollId;

    /**
     * 缴费通知单编号（格式：ORG_PAY_时间戳_随机数）
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
     * 缴费凭证URL（考生上传的缴费截图/凭证）
     */
    private String paymentProofUrl;

    /**
     * 缴费通知单URL（生成的缴费通知PDF地址）
     */
    private String auditNoticeUrl;

    /**
     * 审核状态：0-待缴费，1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核，5-退款审核，6-已退款，7-退款驳回
     */
    private Integer auditStatus;

    /**
     * 驳回原因（审核驳回/退款驳回时填写）
     */
    private String rejectReason;

    /**
     * 审核人ID（关联管理员表主键）
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
}