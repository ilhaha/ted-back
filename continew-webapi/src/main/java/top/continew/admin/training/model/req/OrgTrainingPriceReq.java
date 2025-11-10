package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 创建或修改机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）参数
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
@Data
@Schema(description = "创建或修改机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）参数")
public class OrgTrainingPriceReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 八大类项目ID（关联八大类项目字典表主键）
     */
    @Schema(description = "八大类项目ID（关联八大类字典表主键）")
    private Long projectId;

    /**
     * 机构ID（关联机构表主键）
     */
    @Schema(description = "机构ID（关联机构表主键）")
    private Long orgId;

    /**
     * 培训价格（元，精确到分，对应“价格表”核心需求）
     */
    @Schema(description = "培训价格（元，精确到分，对应“价格表”核心需求）")
    @NotNull(message = "培训价格（元，精确到分，对应“价格表”核心需求）不能为空")
    private BigDecimal price;
}