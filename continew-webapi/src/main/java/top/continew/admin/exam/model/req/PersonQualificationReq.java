package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改人员复审信息表参数
 *
 * @author ilhaha
 * @since 2025/12/29 09:23
 */
@Data
@Schema(description = "创建或修改人员复审信息表参数")
public class PersonQualificationReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Length(max = 50, message = "姓名长度不能超过 {max} 个字符")
    private String name;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;


    /**
     * 文化程度
     */
    @Schema(description = "文化程度")
    private String education;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 聘用单位
     */
    @Schema(description = "聘用单位")
    private String employer;

    /**
     * 资格项目代码
     */
    @Schema(description = "资格项目代码")
    private String qualificationCategoryCode;


    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createUser;
}