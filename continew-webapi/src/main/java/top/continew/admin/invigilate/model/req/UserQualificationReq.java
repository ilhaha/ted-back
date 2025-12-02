package top.continew.admin.invigilate.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改监考员资质证明参数
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Data
@Schema(description = "创建或修改监考员资质证明参数")
public class UserQualificationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 八大类ID
     */
    @Schema(description = "八大类ID")
    @NotNull(message = "八大类ID不能为空")
    private Long categoryId;

    /**
     * 资质证明URL
     */
    @Schema(description = "资质证明URL")
    @NotBlank(message = "资质证明URL不能为空")
    @Length(max = 500, message = "资质证明URL长度不能超过 {max} 个字符")
    private String qualificationUrl;
}