package top.continew.admin.worker.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 作业人员准考证查询条件
 *
 * @author ilhaha
 * @since 2025/11/19 15:42
 */
@Data
@Schema(description = "作业人员准考证查询条件")
public class WorkerExamTicketQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    @Query(type = QueryType.EQ)
    private Long enrollId;

    /**
     * 作业人员姓名
     */
    @Schema(description = "作业人员姓名")
    @Query(type = QueryType.EQ)
    private String candidateName;

    /**
     * 准考证地址
     */
    @Schema(description = "准考证地址")
    @Query(type = QueryType.EQ)
    private String ticketUrl;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @Query(type = QueryType.EQ)
    private Integer isDeleted;
}