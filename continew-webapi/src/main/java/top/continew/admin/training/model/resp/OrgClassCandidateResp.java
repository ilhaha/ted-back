package top.continew.admin.training.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 机构班级与考生关联表信息
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Data
@Schema(description = "机构班级与考生关联表信息")
public class OrgClassCandidateResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级id
     */
    @Schema(description = "班级id")
    private Long classId;

    /**
     * 考生id
     */
    @Schema(description = "考生id")
    private Long candidateId;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Integer isDeleted;

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
}