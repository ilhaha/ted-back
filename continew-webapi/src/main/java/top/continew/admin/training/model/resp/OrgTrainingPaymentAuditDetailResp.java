package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）详情信息
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）详情信息")
public class OrgTrainingPaymentAuditDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联机构ID（关联机构表主键）
     */
    @Schema(description = "关联机构ID（关联机构表主键）")
    @ExcelProperty(value = "关联机构ID（关联机构表主键）")
    private Long orgId;

    /**
     * 关联培训ID（关联机构培训价格表主键）
     */
    @Schema(description = "关联培训ID（关联机构培训价格表主键）")
    @ExcelProperty(value = "关联培训ID（关联机构培训价格表主键）")
    private Long trainingId;

    /**
     * 关联八大类ID（关联八大类字典表主键）
     */
    @Schema(description = "关联八大类ID（关联八大类字典表主键）")
    @ExcelProperty(value = "关联八大类ID（关联八大类字典表主键）")
    private Long categoryId;

    /**
     * 考生ID（缴费考生，关联用户表主键）
     */
    @Schema(description = "考生ID（缴费考生，关联用户表主键）")
    @ExcelProperty(value = "考生ID（缴费考生，关联用户表主键）")
    private Long candidateId;

    /**
     * 关联报名记录ID（关联机构培训报名记录表主键）
     */
    @Schema(description = "关联报名记录ID（关联机构培训报名记录表主键）")
    @ExcelProperty(value = "关联报名记录ID（关联机构培训报名记录表主键）")
    private Long enrollId;

    /**
     * 缴费通知单编号（格式：ORG_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号（格式：ORG_PAY_时间戳_随机数）")
    @ExcelProperty(value = "缴费通知单编号（格式：ORG_PAY_时间戳_随机数）")
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
     * 缴费凭证URL（考生上传的缴费截图/凭证）
     */
    @Schema(description = "缴费凭证URL（考生上传的缴费截图/凭证）")
    @ExcelProperty(value = "缴费凭证URL（考生上传的缴费截图/凭证）")
    private String paymentProofUrl;

    /**
     * 缴费通知单URL（生成的缴费通知PDF地址）
     */
    @Schema(description = "缴费通知单URL（生成的缴费通知PDF地址）")
    @ExcelProperty(value = "缴费通知单URL（生成的缴费通知PDF地址）")
    private String auditNoticeUrl;

    /**
     * 审核状态：0-待缴费，1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核，5-退款审核，6-已退款，7-退款驳回
     */
    @Schema(description = "审核状态：0-待缴费，1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核，5-退款审核，6-已退款，7-退款驳回")
    @ExcelProperty(value = "审核状态：0-待缴费，1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核，5-退款审核，6-已退款，7-退款驳回")
    private Integer auditStatus;

    /**
     * 驳回原因（审核驳回/退款驳回时填写）
     */
    @Schema(description = "驳回原因（审核驳回/退款驳回时填写）")
    @ExcelProperty(value = "驳回原因（审核驳回/退款驳回时填写）")
    private String rejectReason;

    /**
     * 审核人ID（关联管理员表主键）
     */
    @Schema(description = "审核人ID（关联管理员表主键）")
    @ExcelProperty(value = "审核人ID（关联管理员表主键）")
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