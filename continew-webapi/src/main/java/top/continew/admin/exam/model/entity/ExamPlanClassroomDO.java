package top.continew.admin.exam.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ted_plan_classroom")
public class ExamPlanClassroomDO {

    /**
     * 考试计划id
     */
    private Long PlanId;

    /**
     * 考场id
     */
    private Long classroomId;
}
