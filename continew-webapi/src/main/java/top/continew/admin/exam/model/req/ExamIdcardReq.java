package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生身份证信息参数
 *
 * @author ilhaha
 * @since 2025/10/20 11:18
 */
@Data
@Schema(description = "创建或修改考生身份证信息参数")
public class ExamIdcardReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Length(max = 64, message = "姓名长度不能超过 {max} 个字符")
    private String realName;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    @NotBlank(message = "身份证号码不能为空")
    @Length(max = 32, message = "身份证号码长度不能超过 {max} 个字符")
    private String idCardNumber;

    /**
     * 性别（男 女）
     */
    private String gender;

    /**
     * 民族
     */
    private String nation;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 住址
     */
    private String address;

    /**
     * 签发机关
     */
    private String issuingAuthority;

    /**
     * 有效期开始日期
     */
    private LocalDate validStartDate;

    /**
     * 有效期截止日期
     */
    private LocalDate validEndDate;

    /**
     * 身份证正面照片路径
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面照片路径
     */
    private String idCardPhotoBack;
}