package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考试劳务费配置参数
 *
 * @author ilhaha
 * @since 2025/12/17 20:07
 */
@Data
@Schema(description = "创建或修改考试劳务费配置参数")
public class ExamViolationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
    @NotNull(message = "计划ID不能为空")
    private Long planId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long candidateId;

    /**
     * 违规描述
     */
    @Schema(description = "违规描述")
    @NotBlank(message = "违规描述不能为空")
    @Length(max = 500, message = "违规描述长度不能超过 {max} 个字符")
    private String violationDesc;
}