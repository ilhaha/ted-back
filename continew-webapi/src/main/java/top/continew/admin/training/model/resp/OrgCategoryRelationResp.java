package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构与八大类关联，记录多对多关系信息
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Data
@Schema(description = "机构与八大类关联，记录多对多关系信息")
public class OrgCategoryRelationResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID，关联ted_org表的id
     */
    @Schema(description = "机构ID，关联ted_org表的id")
    private Long orgId;

    /**
     * 八大类ID，关联ted_category表的id
     */
    @Schema(description = "八大类ID，关联ted_category表的id")
    private Long categoryId;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @Schema(description = "是否删除（0-未删除，1-已删除）")
    private Boolean isDeleted;
}