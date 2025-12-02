package top.continew.admin.invigilate.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 监考员资质证明信息
 *
 * @author ilhaha
 * @since 2025/12/02 16:55
 */
@Data
@Schema(description = "监考员资质证明信息")
public class UserQualificationResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 八大类ID
     */
    @Schema(description = "八大类ID")
    private Long categoryId;

    /**
     * 资质证明URL
     */
    @Schema(description = "资质证明URL")
    private String qualificationUrl;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private Long updateUser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}