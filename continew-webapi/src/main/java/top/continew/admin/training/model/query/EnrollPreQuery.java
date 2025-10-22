package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 机构考生预报名查询条件
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Data
@Schema(description = "机构考生预报名查询条件")
public class EnrollPreQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @Query(type = QueryType.LIKE)
    private Long candidateId;

    /**
     * 计划id
     */
    @Schema(description = "计划id")
    @Query(type = QueryType.EQ)
    private Long planId;
}