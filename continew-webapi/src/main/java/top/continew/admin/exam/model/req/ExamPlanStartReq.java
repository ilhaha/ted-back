package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamPlanStartReq {

    /**
     * 考试计划ID
     */
    @NotNull(message = "未选择考试计划")
    private Long examPlanId;

    /**
     * 开考密码
     */
    @NotBlank(message = "未输入开考密码")
    private String examPassword;

    /**
     * 考场id
     */
    @NotNull(message = "未选择监考考场")
    private Long classroomId;
}