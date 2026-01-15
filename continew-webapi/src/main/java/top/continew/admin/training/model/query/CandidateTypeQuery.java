package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.annotation.QueryIgnore;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生类型查询条件
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Data
@Schema(description = "考生类型查询条件")
public class CandidateTypeQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @Query(type = QueryType.EQ)
    private Long candidateId;

    /**
     * 考生姓名
     */
    @Schema(description = "考生姓名")
    @QueryIgnore
    private String candidateName;

    /**
     * 考生身份证
     */
    @Schema(description = "考生身份证")
    @QueryIgnore
    private String idNumber;
}