package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生类型与禁考项目关联信息
 *
 * @author ilhaha
 * @since 2026/03/02 14:09
 */
@Data
@Schema(description = "考生类型与禁考项目关联信息")
public class CandidateTypeDisableProjectResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生类型ID
     */
    @Schema(description = "考生类型ID")
    private Long candidateTypeId;

    /**
     * 禁考项目ID
     */
    @Schema(description = "禁考项目ID")
    private Long disableProjectId;

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
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Integer isDeleted;
}