package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生报考通知对应项目-计划明细参数
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Data
@Schema(description = "创建或修改考生报考通知对应项目-计划明细参数")
public class ExamineeNoticeApplyRecordReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名主表ID
     */
    @Schema(description = "报名主表ID")
    @NotNull(message = "报名主表ID不能为空")
    private Long applyId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @NotNull(message = "考生ID不能为空")
    private Long examineeId;

    /**
     * 通知ID
     */
    @Schema(description = "通知ID")
    @NotNull(message = "通知ID不能为空")
    private Long noticeId;

    /**
     * 报考项目ID
     */
    @Schema(description = "报考项目ID")
    @NotNull(message = "报考项目ID不能为空")
    private Long projectId;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @NotNull(message = "考试计划ID不能为空")
    private Long planId;

    /**
     * 考试场次类型：1初试，2补考
     */
    @Schema(description = "考试场次类型：1初试，2补考")
    @NotNull(message = "考试场次类型：1初试，2补考不能为空")
    private Integer examAttemptType;

    /**
     * 报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷
     */
    @Schema(description = "报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷")
    @NotNull(message = "报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷不能为空")
    private Integer practicalType;
}