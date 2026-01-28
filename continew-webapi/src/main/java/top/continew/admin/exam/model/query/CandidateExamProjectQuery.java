package top.continew.admin.exam.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 考生-考试项目考试状态查询条件
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Data
@Schema(description = "考生-考试项目考试状态查询条件")
public class CandidateExamProjectQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @Query(type = QueryType.EQ)
    private Long candidateId;

    /**
     * 考试项目ID
     */
    @Schema(description = "考试项目ID")
    @Query(type = QueryType.EQ)
    private Long projectId;

    /**
     * 当前考试轮次（0表示未开始）
     */
    @Schema(description = "当前考试轮次（0表示未开始）")
    @Query(type = QueryType.EQ)
    private Integer attemptNo;

    /**
     * 当前轮次是否已补考：0-否 1-是
     */
    @Schema(description = "当前轮次是否已补考：0-否 1-是")
    @Query(type = QueryType.EQ)
    private Boolean usedMakeup;

    /**
     * 是否通过：0-未通过 1-通过
     */
    @Schema(description = "是否通过：0-未通过 1-通过")
    @Query(type = QueryType.EQ)
    private Boolean passed;

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