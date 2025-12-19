package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考试劳务费配置实体
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@TableName("ted_exam_violation")
public class ExamViolationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 考生ID
     */
    private Long candidateId;

    /**
     * 违规描述
     */
    private String violationDesc;

    /**
     * 违规图片
     */
    private String illegalUrl;

    /**
     * 逻辑删除
     */
    private Integer isDeleted;

}