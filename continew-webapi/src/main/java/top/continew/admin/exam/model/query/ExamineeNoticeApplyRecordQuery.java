package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生报考通知对应项目-计划明细查询条件
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Data
@Schema(description = "考生报考通知对应项目-计划明细查询条件")
public class ExamineeNoticeApplyRecordQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名主表ID
     */
    @Schema(description = "报名主表ID")
    @Query(type = QueryType.EQ)
    private Long applyId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long examineeId;

    /**
     * 通知ID
     */
    @Schema(description = "通知ID")
    @Query(type = QueryType.EQ)
    private Long noticeId;

    /**
     * 报考项目ID
     */
    @Schema(description = "报考项目ID")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @Query(type = QueryType.EQ)
    private Long planId;
}