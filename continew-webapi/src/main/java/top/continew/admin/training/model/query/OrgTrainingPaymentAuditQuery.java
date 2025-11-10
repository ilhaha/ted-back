package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）查询条件
 *
 * @author ilhaha
 * @since 2025/11/10 09:04
 */
@Data
@Schema(description = "机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）查询条件")
public class OrgTrainingPaymentAuditQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联机构ID（关联机构表主键）
     */
    @Schema(description = "关联机构ID（关联机构表主键）")
    @Query(type = QueryType.EQ)
    private Long orgId;

    /**
     * 关联培训ID（关联机构培训价格表主键）
     */
    @Schema(description = "关联培训ID（关联机构培训价格表主键）")
    @Query(type = QueryType.EQ)
    private Long trainingId;

    /**
     * 关联八大类项目ID（关联八大类字典表主键）
     */
    @Schema(description = "关联八大类项目ID（关联八大类项目字典表主键）")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 考生ID（缴费考生，关联用户表主键）
     */
    @Schema(description = "考生ID（缴费考生，关联用户表主键）")
    @Query(type = QueryType.EQ)
    private Long candidateId;

    /**
     * 关联报名记录ID（关联机构培训报名记录表主键）
     */
    @Schema(description = "关联报名记录ID（关联机构培训报名记录表主键）")
    @Query(type = QueryType.EQ)
    private Long enrollId;

    /**
     * 缴费通知单编号（格式：ORG_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号（格式：ORG_PAY_时间戳_随机数）")
    @Query(type = QueryType.EQ)
    private String noticeNo;

    /**
     * 缴费金额（元）
     */
    @Schema(description = "缴费金额（元）")
    @Query(type = QueryType.EQ)
    private BigDecimal paymentAmount;
}