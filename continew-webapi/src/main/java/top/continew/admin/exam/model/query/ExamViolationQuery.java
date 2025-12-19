package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考试劳务费配置查询条件
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@Schema(description = "考试劳务费配置查询条件")
public class ExamViolationQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @Query(type = QueryType.EQ)
    private Long planId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long candidateId;

    /**
     * 违规描述
     */
    @Schema(description = "违规描述")
    @Query(type = QueryType.EQ)
    private String violationDesc;
}