package top.continew.admin.exam.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/10/16 15:31
 *
 * 机构获取符合自身八大类的考试计划VO
 */
@Data
public class OrgExamPlanVO {

    /**
     * 计划id
     */
    private Long id;

    /**
     * 考试计划名称
     */
    private String examPlanName;


    /**
     * 考试开始时间
     */
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    private LocalDateTime endTime;


    /**
     * 计划状态
     */
    private Integer status;


    /**
     * 报名开始时间
     */
    private LocalDateTime enrollStartTime;

    /**
     * 报名结束时间
     */
    private LocalDateTime enrollEndTime;

    /**
     * 描述
     */
    private String redeme;


}
