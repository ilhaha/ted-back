package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改持证信息参数
 *
 * @author ilhaha
 * @since 2026/05/08 15:26
 */
@Data
@Schema(description = "创建或修改持证信息参数")
public class LicenseHolderInfoReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long examineeId;

    /**
     * 持证项目编码
     */
    @Schema(description = "持证项目编码")
    @NotBlank(message = "持证项目编码不能为空")
    @Length(max = 100, message = "持证项目编码长度不能超过 {max} 个字符")
    private String projectCode;

    /**
     * 项目等级  0-无 1一级 2 二级
     */
    @Schema(description = "项目等级  0-无 1一级 2 二级")
    @NotNull(message = "项目等级  0-无 1一级 2 二级不能为空")
    private Integer projectLevel;

    /**
     * 有效开始日期
     */
    @Schema(description = "有效开始日期")
    @NotNull(message = "有效开始日期不能为空")
    private LocalDate validStartDate;

    /**
     * 有效结束日期
     */
    @Schema(description = "有效结束日期")
    @NotNull(message = "有效结束日期不能为空")
    private LocalDate validEndDate;
}