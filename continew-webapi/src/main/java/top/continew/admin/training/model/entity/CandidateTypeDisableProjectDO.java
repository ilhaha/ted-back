package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考生类型与禁考项目关联实体
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Data
@TableName("ted_candidate_type_disable_project")
public class CandidateTypeDisableProjectDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生类型ID
     */
    private Long candidateTypeId;

    /**
     * 禁考项目ID
     */
    private Long disableProjectId;

    /**
     * 删除标记
     */
    private Integer isDeleted;
}