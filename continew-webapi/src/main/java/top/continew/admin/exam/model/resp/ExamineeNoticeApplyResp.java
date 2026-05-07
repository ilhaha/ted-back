package top.continew.admin.exam.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;
import java.util.List;

/**
 * 考生资料关系信息
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Data
@Schema(description = "考生资料关系信息")
public class ExamineeNoticeApplyResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试
     */
    @Schema(description = "状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试")
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    private String remark;

    /**
     * 报考的具体项目
     */
    @Schema(description = "报考的具体项目")
    private List<ExamineeNoticeApplyRecordResp> noticeApplyRecordRespList;
}