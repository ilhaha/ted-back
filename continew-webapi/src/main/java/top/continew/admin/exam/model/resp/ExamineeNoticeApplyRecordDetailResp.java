package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 考生报考通知对应项目-计划明细详情信息
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生报考通知对应项目-计划明细详情信息")
public class ExamineeNoticeApplyRecordDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名主表ID
     */
    @Schema(description = "报名主表ID")
    @ExcelProperty(value = "报名主表ID")
    private Long applyId;

    /**
     * 考生ID
     */
    @Schema(description = "考生ID")
    @ExcelProperty(value = "考生ID")
    private Long examineeId;

    /**
     * 通知ID
     */
    @Schema(description = "通知ID")
    @ExcelProperty(value = "通知ID")
    private Long noticeId;

    /**
     * 报考项目ID
     */
    @Schema(description = "报考项目ID")
    @ExcelProperty(value = "报考项目ID")
    private Long projectId;

    /**
     * 考试计划ID
     */
    @Schema(description = "考试计划ID")
    @ExcelProperty(value = "考试计划ID")
    private Long planId;

    /**
     * 考试场次类型：1初试，2补考
     */
    @Schema(description = "考试场次类型：1初试，2补考")
    @ExcelProperty(value = "考试场次类型：1初试，2补考")
    private Integer examAttemptType;

    /**
     * 报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷
     */
    @Schema(description = "报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷")
    @ExcelProperty(value = "报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷")
    private Integer practicalType;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}