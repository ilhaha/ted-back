package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生类型与禁考项目关联查询条件
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Data
@Schema(description = "考生类型与禁考项目关联查询条件")
public class CandidateTypeDisableProjectQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生类型ID
     */
    @Schema(description = "考生类型ID")
    @Query(type = QueryType.EQ)
    private Long candidateTypeId;

    /**
     * 禁考项目ID
     */
    @Schema(description = "禁考项目ID")
    @Query(type = QueryType.EQ)
    private Long disableProjectId;
}