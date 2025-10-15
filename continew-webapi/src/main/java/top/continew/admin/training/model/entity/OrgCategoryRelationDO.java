package top.continew.admin.training.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 机构与八大类关联，记录多对多关系实体
 *
 * @author hoshi
 * @since 2025/09/01 16:14
 */
@Data
@TableName("ted_org_category_relation")
public class OrgCategoryRelationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID，关联ted_org表的id
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 八大类ID，关联ted_category表的id
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Boolean isDeleted;

}