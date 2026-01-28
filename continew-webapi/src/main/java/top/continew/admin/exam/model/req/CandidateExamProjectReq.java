package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生-考试项目考试状态参数
 *
 * @author ilhaha
 * @since 2026/01/28 14:12
 */
@Data
@Schema(description = "创建或修改考生-考试项目考试状态参数")
public class CandidateExamProjectReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long candidateId;

    /**
     * 考试项目ID
     */
    @Schema(description = "考试项目ID")
    @NotNull(message = "考试项目ID不能为空")
    private Long projectId;

    /**
     * 当前考试轮次（0表示未开始）
     */
    @Schema(description = "当前考试轮次（0表示未开始）")
    @NotNull(message = "当前考试轮次（0表示未开始）不能为空")
    private Integer attemptNo;

    /**
     * 当前轮次是否已补考：0-否 1-是
     */
    @Schema(description = "当前轮次是否已补考：0-否 1-是")
    @NotNull(message = "当前轮次是否已补考：0-否 1-是不能为空")
    private Boolean usedMakeup;

    /**
     * 是否通过：0-未通过 1-通过
     */
    @Schema(description = "是否通过：0-未通过 1-通过")
    @NotNull(message = "是否通过：0-未通过 1-通过不能为空")
    private Boolean passed;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @NotNull(message = "创建时间不能为空")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @NotNull(message = "更新时间不能为空")
    private LocalDateTime updateTime;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @NotNull(message = "删除标记(0未删,1已删)不能为空")
    private Integer isDeleted;
}