package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生试卷查询条件
 *
 * @author ilhaha
 * @since 2025/11/19 16:05
 */
@Data
@Schema(description = "考生试卷查询条件")
public class CandidateExamPaperQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名id
     */
    @Schema(description = "报名id")
    @Query(type = QueryType.EQ)
    private Long enrollId;

    /**
     * 试卷 JSON 内容
     */
    @Schema(description = "试卷 JSON 内容")
    @Query(type = QueryType.EQ)
    private String paperJson;

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