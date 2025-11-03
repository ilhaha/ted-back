package top.continew.admin.worker.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改作业人员报名参数
 *
 * @author ilhaha
 * @since 2025/11/03 11:15
 */
@Data
@Schema(description = "创建或修改作业人员报名参数")
public class WorkerApplyReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @NotBlank(message = "作业人员姓名不能为空")
    @Length(max = 255, message = "作业人员姓名长度不能超过 {max} 个字符")
    private String candidateName;

    /**
     * 作业人员性别
     */
    @Schema(description = "作业人员性别")
    @NotBlank(message = "作业人员性别不能为空")
    @Length(max = 1, message = "作业人员性别长度不能超过 {max} 个字符")
    private String gender;

    /**
     * 作业人员手机号
     */
    @Schema(description = "作业人员手机号")
    @NotBlank(message = "作业人员手机号不能为空")
    @Length(max = 50, message = "作业人员手机号长度不能超过 {max} 个字符")
    private String phone;

    /**
     * 报名资格申请表名称
     */
    @Schema(description = "报名资格申请表名称")
    @NotBlank(message = "报名资格申请表名称不能为空")
    @Length(max = 255, message = "报名资格申请表名称长度不能超过 {max} 个字符")
    private String qualificationName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @NotBlank(message = "身份证号不能为空")
    @Length(max = 255, message = "身份证号长度不能超过 {max} 个字符")
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    @Schema(description = "身份证正面存储地址")
    @NotBlank(message = "身份证正面存储地址不能为空")
    @Length(max = 255, message = "身份证正面存储地址长度不能超过 {max} 个字符")
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    @Schema(description = "身份证反面存储地址")
    @NotBlank(message = "身份证反面存储地址不能为空")
    @Length(max = 255, message = "身份证反面存储地址长度不能超过 {max} 个字符")
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    @Schema(description = "一寸免冠照存储地址")
    @NotBlank(message = "一寸免冠照存储地址不能为空")
    @Length(max = 255, message = "一寸免冠照存储地址长度不能超过 {max} 个字符")
    private String facePhoto;
}