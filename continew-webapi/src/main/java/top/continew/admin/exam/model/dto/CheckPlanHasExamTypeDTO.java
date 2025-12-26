package top.continew.admin.exam.model.dto;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/12/25 9:34
 */
@Data
public class CheckPlanHasExamTypeDTO {

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 是否有实操成绩
     */
    private Integer isOperation;

    /**
     * 是否有道路成绩
     */
    private Integer isRoad;
}
