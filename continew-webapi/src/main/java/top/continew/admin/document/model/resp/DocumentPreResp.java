package top.continew.admin.document.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构报考-考生上传资料信息
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@Schema(description = "机构报考-考生上传资料信息")
public class DocumentPreResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构报考-考生扫码上传文件表id
     */
    @Schema(description = "机构报考-考生扫码上传文件表id")
    private Long enrollPreUploadId;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    private Long typeId;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}