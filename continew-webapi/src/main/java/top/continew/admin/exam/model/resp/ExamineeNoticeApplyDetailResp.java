package top.continew.admin.exam.model.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import org.hibernate.validator.constraints.Length;
import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;
import java.util.List;

/**
 * 考生资料关系详情信息
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "考生资料关系详情信息")
public class ExamineeNoticeApplyDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;

    private String nickname;
    /**
     * JSON字符串（数据库接收）
     */
    @JsonIgnore
    private String projectExamListJson;

    /**
     * 报考序号
     */
    private Long sort;

    /**
     * 项目考试信息
     */
    private List<ProjectExamResp> projectExamList;

    /**
     * 考生报考情况
     */
    private List<NoticeExamProjectResp> noticeExamProjectList;

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
     * 状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试
     */
    @Schema(description = "状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试")
    @ExcelProperty(value = "状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试")
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    @ExcelProperty(value = "审核意见或退回原因")
    private String remark;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}