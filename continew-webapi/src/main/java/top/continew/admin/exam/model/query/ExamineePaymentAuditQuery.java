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
     * 审核状态：0-待审核，1-审核通过，2-审核驳回
     */
    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核驳回")
    @Query(type = QueryType.EQ)
    private Integer auditStatus;
}