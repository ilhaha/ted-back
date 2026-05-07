package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生报考通知对应项目-计划明细信息
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Data
@Schema(description = "考生报考通知对应项目-计划明细信息")
public class ExamineeNoticeApplyRecordResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名主表ID
     */
    @Schema(description = "报名主表ID")
    private Long applyId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    private Long examineeId;

    /**
     * 通知ID
     */
    @Schema(description = "通知ID")
    private Long noticeId;

    /**
     * 报考项目ID
     */
    @Schema(description = "报考项目ID")
    private Long projectId;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    private Long planId;

    /**
     * 考试场次类型：1初试，2补考
     */
    @Schema(description = "考试场次类型：1初试，2补考")
    private Integer examAttemptType;

    /**
     * 报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷
     */
    @Schema(description = "报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷")
    private Integer practicalType;
}