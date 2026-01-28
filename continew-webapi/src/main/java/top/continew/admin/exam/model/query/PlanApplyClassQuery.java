package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考试计划报考班级查询条件
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Data
@Schema(description = "考试计划报考班级查询条件")
public class PlanApplyClassQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划名称
     */
    @Schema(description = "考试计划名称")
    @Query(type = QueryType.LIKE,columns = "tep.exam_plan_name")
    private String planName;

}