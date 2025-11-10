package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）实体
 *
 * @author ilhaha
 * @since 2025/11/10 08:55
 */
@Data
@TableName("ted_org_training_price")
public class OrgTrainingPriceDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 八大类ID（关联八大类字典表主键）
     */
    private Long categoryId;

    /**
     * 机构ID（关联机构表主键）
     */
    private Long orgId;

    /**
     * 培训价格（元，精确到分，对应“价格表”核心需求）
     */
    private BigDecimal price;

    /**
     * 是否删除（0否，1是）
     */
    private Boolean isDeleted;
}