package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考试计划报考班级实体
 *
 * @author ilhaha
 * @since 2026/01/28 09:17
 */
@Data
@TableName("ted_plan_apply_class")
public class PlanApplyClassDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 是否已确认成绩（0：未确认 1：已确认）
     */
    private Integer isScoreConfirmed;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
}