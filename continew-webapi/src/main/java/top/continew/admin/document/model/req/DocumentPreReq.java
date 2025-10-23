package top.continew.admin.document.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建或修改机构报考-考生上传资料参数
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "创建或修改机构报考-考生上传资料参数")
public class DocumentPreReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构报考-考生扫码上传文件表id
     */
    @Schema(description = "机构报考-考生扫码上传文件表id")
    @NotNull(message = "机构报考-考生扫码上传文件表id不能为空")
    private Long enrollPreUploadId;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    @NotBlank(message = "存储路径不能为空")
    @Length(max = 2555, message = "存储路径长度不能超过 {max} 个字符")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    @NotNull(message = "关联资料类型ID不能为空")
    private Long typeId;
}