package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改机构申请焊接考试项目参数
 *
 * @author ilhaha
 * @since 2026/01/16 10:58
 */
@Data
@Schema(description = "创建或修改机构申请焊接考试项目参数")
public class WeldingExamApplicationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请机构ID
     */
    @Schema(description = "申请机构ID")
//    @NotNull(message = "申请机构ID不能为空")
    private Long orgId;

    /**
     * 焊接类型：0-金属焊接，1-非金属焊接
     */
    @Schema(description = "焊接类型：0-金属焊接，1-非金属焊接")
    @NotNull(message = "焊接类型：0-金属焊接，1-非金属焊接不能为空")
    private Integer weldingType;

    /**
     * 焊接考试项目名称
     */
    @Schema(description = "焊接考试项目名称")
//    @NotBlank(message = "焊接考试项目名称不能为空")
//    @Length(max = 100, message = "焊接考试项目名称长度不能超过 {max} 个字符")
    private String projectName;

    /**
     * 考试项目代码
     */
    @Schema(description = "考试项目代码")
    @NotBlank(message = "考试项目代码不能为空")
    @Length(max = 255, message = "考试项目代码长度不能超过 {max} 个字符")
    private String projectCode;

    /**
     * 申请原因或说明
     */
    @Schema(description = "申请原因或说明")
    @NotBlank(message = "申请原因或说明不能为空")
    @Length(max = 200, message = "申请原因或说明长度不能超过 {max} 个字符")
    private String applicationReason;
}