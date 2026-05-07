package top.continew.admin.exam.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改考生资料关系参数
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Data
@Schema(description = "创建或修改考生资料关系参数")
public class ExamineeNoticeApplyReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试
     */
    @Schema(description = "状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试")
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    @Schema(description = "审核意见或退回原因")
    @Length(max = 255, message = "审核意见或退回原因长度不能超过 {max} 个字符")
    private String remark;
}