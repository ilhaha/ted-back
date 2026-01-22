package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改焊接项目实操成绩参数
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Data
@Schema(description = "创建或修改焊接项目实操成绩参数")
public class WeldingOperScoreReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @NotNull(message = "考试计划ID不能为空")
    private Long planId;

    /**
     * 考试记录ID
     */
    @Schema(description = "考试记录ID")
    @NotNull(message = "考试记录ID不能为空")
    private Long recordId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long candidateId;

    /**
     * 焊接项目代码
     */
    @Schema(description = "焊接项目代码")
    @NotBlank(message = "焊接项目代码不能为空")
    @Length(max = 50, message = "焊接项目代码长度不能超过 {max} 个字符")
    private String projectCode;
}