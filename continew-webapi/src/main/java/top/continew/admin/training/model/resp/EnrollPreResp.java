package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构考生预报名信息
 *
 * @author ilhaha
 * @since 2025/10/22 10:22
 */
@Data
@Schema(description = "机构考生预报名信息")
public class EnrollPreResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    private Long candidateId;

    /**
     * 计划id
     */
    @Schema(description = "计划id")
    private Long planId;

    /**
     * 上传资料二维码
     */
    @Schema(description = "上传资料二维码")
    private String uploadQrcode;

    /**
     * 资料上传状态 0-资料待补充 1-报考资料已齐全
     */
    @Schema(description = "资料上传状态 0-资料待补充 1-报考资料已齐全")
    private Integer status;

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
     * 逻辑删除 0-未删除 1-已删除
     */
    @Schema(description = "逻辑删除 0-未删除 1-已删除")
    private Integer isDeleted;
}