package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生试卷信息
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
@Data
@Schema(description = "考生试卷信息")
public class CandidateExamPaperResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    private Long enrollId;

    /**
     * 试卷 JSON 内容
     */
    @Schema(description = "试卷 JSON 内容")
    private String paperJson;

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
    private Integer isDeleted;
}