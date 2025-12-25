package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改人员及许可证书信息参数
 *
 * @author ilhaha
 * @since 2025/12/25 14:13
 */
@Data
@Schema(description = "创建或修改人员及许可证书信息参数")
public class LicenseCertificateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Length(max = 50, message = "姓名长度不能超过 {max} 个字符")
    private String psnName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @NotBlank(message = "身份证号不能为空")
    @Length(max = 255, message = "身份证号长度不能超过 {max} 个字符")
    private String idcardNo;
}