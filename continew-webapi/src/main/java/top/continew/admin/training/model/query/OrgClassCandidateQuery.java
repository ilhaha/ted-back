package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 机构班级与考生关联表查询条件
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Data
@Schema(description = "机构班级与考生关联表查询条件")
public class OrgClassCandidateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级id
     */
    @Schema(description = "班级id")
    @Query(type = QueryType.EQ)
    private Long classId;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @Query(type = QueryType.LIKE)
    private Long candidateId;
}