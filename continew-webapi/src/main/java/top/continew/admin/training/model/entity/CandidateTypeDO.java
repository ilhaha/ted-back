package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考生类型实体
 *
 * @author ilhaha
 * @since 2025/11/03 17:57
 */
@Data
@TableName("ted_candidate_type")
public class CandidateTypeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 类型，0作业人员，1检验人员
     */
    private Boolean type;

    /**
     * 删除标记
     */
    private Integer isDeleted;
}