package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 创建或修改机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）参数
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Data
@Schema(description = "创建或修改机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）参数")
public class OrgTrainingPaymentAuditReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联机构ID（关联机构表主键）
     */
    @Schema(description = "关联机构ID（关联机构表主键）")
    private Long orgId;

    /**
     * 关联培训ID（关联机构培训价格表主键）
     */
    @Schema(description = "关联培训ID（关联机构培训价格表主键）")
    private Long trainingId;

    /**
     * 关联八大类项目ID（关联八大类字典表主键）
     */
    @Schema(description = "关联八大类项目ID（关联八大类项目字典表主键）")
    private Long projectId;

    /**
     * 考生ID（缴费考生，关联用户表主键）
     */
    @Schema(description = "考生ID（缴费考生，关联用户表主键）")
    @NotNull(message = "考生ID（缴费考生，关联用户表主键）不能为空")
    private Long candidateId;

    /**
     * 关联报名记录ID（关联机构培训报名记录表主键）
     */
    @Schema(description = "关联报名记录ID（关联机构培训报名记录表主键）")
    @NotNull(message = "关联报名记录ID（关联机构培训报名记录表主键）不能为空")
    private Long enrollId;

    /**
     * 缴费通知单编号（格式：ORG_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号（格式：ORG_PAY_时间戳_随机数）")
    @NotBlank(message = "缴费通知单编号（格式：ORG_PAY_时间戳_随机数）不能为空")
    @Length(max = 64, message = "缴费通知单编号（格式：ORG_PAY_时间戳_随机数）长度不能超过 {max} 个字符")
    private String noticeNo;

    /**
     * 缴费金额（元）
     */
    @Schema(description = "缴费金额（元）")
    @NotNull(message = "缴费金额（元）不能为空")
    private BigDecimal paymentAmount;
}