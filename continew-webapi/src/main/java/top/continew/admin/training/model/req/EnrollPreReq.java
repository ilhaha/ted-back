package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改机构考生预报名参数
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Data
@Schema(description = "创建或修改机构考生预报名参数")
public class EnrollPreReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @NotNull(message = "考生id不能为空")
    private Long candidateId;

    /**
     * 计划id
     */
    @Schema(description = "计划id")
    @NotNull(message = "计划id不能为空")
    private Long planId;
}