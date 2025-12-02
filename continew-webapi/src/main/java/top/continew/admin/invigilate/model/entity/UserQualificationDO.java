package top.continew.admin.invigilate.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 监考员资质证明实体
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Data
@TableName("ted_user_qualification")
public class UserQualificationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 八大类ID
     */
    private Long categoryId;

    /**
     * 资质证明URL
     */
    private String qualificationUrl;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}