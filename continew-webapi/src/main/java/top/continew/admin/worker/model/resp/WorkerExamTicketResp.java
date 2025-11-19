package top.continew.admin.worker.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 作业人员准考证信息
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Data
@Schema(description = "作业人员准考证信息")
public class WorkerExamTicketResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    private Long enrollId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    private String candidateName;

    /**
     * 准考证地址
     */
    @Schema(description = "准考证地址")
    private String ticketUrl;

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