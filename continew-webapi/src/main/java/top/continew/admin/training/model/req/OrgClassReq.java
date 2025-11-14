package top.continew.admin.training.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改培训机构班级参数
 *
 * @author ilhaha
 * @since 2025/10/17 17:43
 */
@Data
@Schema(description = "创建或修改培训机构班级参数")
public class OrgClassReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    @NotNull(message = "项目id不能为空")
    private Long projectId;

    /**
     * 机构id
     */
    @Schema(description = "机构id")
    private Long orgId;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    @NotBlank(message = "班级名称不能为空")
    @Length(max = 255, message = "班级名称长度不能超过 {max} 个字符")
    private String className;

    /**
     * 班级类型，0作业人员班级，1检验人员班级
     */
    @Schema(description = "班级类型，0作业人员班级，1检验人员班级")
    private Integer classType;

    /**
     * 状态，0招生找，1停止招生
     */
    @Schema(description = "状态，0招生找，1停止招生")
    private Integer status;
}