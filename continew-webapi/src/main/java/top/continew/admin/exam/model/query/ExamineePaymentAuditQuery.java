package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 考生缴费审核查询条件
 *
 * @author ilhaha
 * @since 2025/11/04 10:17
 */
@Data
@Schema(description = "考生缴费审核查询条件")
public class ExamineePaymentAuditQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联考试计划ID
     */
    @Schema(description = "关联考试计划ID")
    @Query(type = QueryType.EQ)
    private Long examPlanId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long examineeId;

    /**
     * 缴费通知单编号（格式：TZSB_PAY_时间戳_随机数）
     */
    @Schema(description = "缴费通知单编号")
    @Query(type = QueryType.EQ)
    private String noticeNo;

    /**
     * 审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回
     */
    @Schema(description = "审核状态：0-待缴费 1-已缴费待审核，2-审核通过，3-审核驳回，4-补正审核 ，5-退款审核， 6-已退款, 7-退款驳回 ")
    @Query(type = QueryType.EQ)
    private Integer auditStatus;

    @Schema(description = "考生姓名查询")
    @Query(type = QueryType.LIKE,columns = "su.nickname")
    private String examineeName; // 考生姓名查询

    @Schema(description = "考试计划名称查询")
    @Query(type = QueryType.LIKE,columns = "tep.exam_plan_name")
    private String planName; // 考试计划名称查询

}