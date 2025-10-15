package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改机构与八大类关联，记录多对多关系参数
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Data
@Schema(description = "创建或修改机构与八大类关联，记录多对多关系参数")
public class OrgCategoryRelationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID，关联ted_org表的id
     */
    @Schema(description = "机构ID，关联ted_org表的id")
    @NotNull(message = "机构ID，关联ted_org表的id不能为空")
    private Long orgId;

    /**
     * 八大类ID，关联ted_category表的id
     */
    @Schema(description = "八大类ID，关联ted_category表的id")
    @NotNull(message = "八大类ID，关联ted_category表的id不能为空")
    private Long categoryId;
}