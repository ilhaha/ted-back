package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生类型参数
 *
 * @author ilhaha
 * @since 2025/11/03 17:57
 */
@Data
@Schema(description = "创建或修改考生类型参数")
public class CandidateTypeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    @NotNull(message = "考生id不能为空")
    private Long candidateId;
}