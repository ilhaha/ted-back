package top.continew.admin.document.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建或修改机构报考-考生扫码上传文件参数
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "创建或修改机构报考-考生扫码上传文件参数")
public class EnrollPreUploadReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long candidatesId;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @NotNull(message = "考试计划ID不能为空")
    private Long planId;

    /**
     * 报名资格申请表URL
     */
    @Schema(description = "报名资格申请表URL")
    @NotBlank(message = "报名资格申请表URL不能为空")
    @Length(max = 255, message = "报名资格申请表URL长度不能超过 {max} 个字符")
    private String qualificationFileUrl;
}