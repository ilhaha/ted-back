package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 焊接项目实操成绩实体
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Data
@TableName("ted_welding_oper_score")
public class WeldingOperScoreDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 考试记录ID
     */
    private Long recordId;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 焊接项目代码
     */
    private String projectCode;

    /**
     * 实操成绩
     */
    private Integer operScore;

    /**
     * 删除标记
     */
    private Boolean isDeleted;
}