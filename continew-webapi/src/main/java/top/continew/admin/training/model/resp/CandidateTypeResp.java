package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生类型信息
 *
 * @author ilhaha
 * @since 2025/11/03 17:57
 */
@Data
@Schema(description = "考生类型信息")
public class CandidateTypeResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    private Long candidateId;

    /**
     * 类型，0作业人员，1检验人员
     */
    @Schema(description = "类型，0作业人员，1检验人员")
    private Boolean type;

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