package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生类型与禁考项目关联参数
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Data
@Schema(description = "创建或修改考生类型与禁考项目关联参数")
public class CandidateTypeDisableProjectReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生类型ID
     */
    @Schema(description = "考生类型ID")
    @NotNull(message = "考生类型ID不能为空")
    private Long candidateTypeId;

    /**
     * 禁考项目ID
     */
    @Schema(description = "禁考项目ID")
    @NotNull(message = "禁考项目ID不能为空")
    private Long disableProjectId;
}