package top.continew.admin.training.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 机构与八大类关联，记录多对多关系查询条件
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Data
@Schema(description = "机构与八大类关联，记录多对多关系查询条件")
public class OrgCategoryRelationQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID，关联ted_org表的id
     */
    @Schema(description = "机构ID，关联ted_org表的id")
    @Query(type = QueryType.EQ)
    private Long orgId;

    /**
     * 八大类ID，关联ted_category表的id
     */
    @Schema(description = "八大类ID，关联ted_category表的id")
    @Query(type = QueryType.EQ)
    private Long categoryId;
}