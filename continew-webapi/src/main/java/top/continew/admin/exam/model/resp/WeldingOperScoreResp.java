package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 焊接项目实操成绩信息
 *
 * @author ilhaha
 * @since 2026/01/21 14:52
 */
@Data
@Schema(description = "焊接项目实操成绩信息")
public class WeldingOperScoreResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    private Long planId;

    /**
     * 考试记录ID
     */
    @Schema(description = "考试记录ID")
    private Long recordId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long candidateId;

    /**
     * 焊接项目代码
     */
    @Schema(description = "焊接项目代码")
    private String projectCode;

    /**
     * 实操成绩
     */
    @Schema(description = "实操成绩")
    private Integer operScore;

    /**
     * 删除标记
     */
    @Schema(description = "删除标记")
    private Boolean isDeleted;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateUser;

    /**
     * 更新时间戳
     */
    @Schema(description = "更新时间戳")
    private LocalDateTime updateTime;
}