package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 机构班级与考生关联表实体
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Data
@TableName("ted_org_class_candidate")
public class OrgClassCandidateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 考生id
     */
    private Long candidateId;

    /**
     * 删除标记
     */
    private Integer isDeleted;

    /**
     * 状态：0 正在班级 1 考试结束之之前转去别的班 2 考试结束之后转去别的班
     */
    private Integer status;
}