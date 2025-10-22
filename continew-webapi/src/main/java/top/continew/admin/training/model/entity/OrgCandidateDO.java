package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 机构考生关联实体
 *
 * @author ilhaha
 * @since 2025/10/21 13:52
 */
@Data
@TableName("ted_org_candidate")
public class OrgCandidateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 机构对应的项目id
     */
    private Long projectId;

    /**
     * 状态 (负1-拒绝, 0-退出，1-待通过，2-已加入)
     */
    private Integer status;

    /**
     * 审核备注
     */
    private String remark;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Boolean isDeleted;
}