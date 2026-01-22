package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 焊接项目实操成绩查询条件
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Data
@Schema(description = "焊接项目实操成绩查询条件")
public class WeldingOperScoreQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @Query(type = QueryType.EQ)
    private Long planId;

    /**
     * 考试记录ID
     */
    @Schema(description = "考试记录ID")
    @Query(type = QueryType.EQ)
    private Long recordId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long candidateId;

    /**
     * 焊接项目代码
     */
    @Schema(description = "焊接项目代码")
    @Query(type = QueryType.EQ)
    private String projectCode;
}