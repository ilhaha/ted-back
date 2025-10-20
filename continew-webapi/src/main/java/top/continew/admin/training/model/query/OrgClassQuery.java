package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 培训机构班级查询条件
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@Schema(description = "培训机构班级查询条件")
public class OrgClassQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    @Query(type = QueryType.LIKE)
    private String className;
}