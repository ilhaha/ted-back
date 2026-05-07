package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考生资料关系实体
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Data
@TableName("ted_examinee_notice_apply")
public class ExamineeNoticeApplyDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考生ID
     */
    private Long examineeId;

    /**
     * 通知ID
     */
    private Long noticeId;

    /**
     * 状态:0待报名,1报名待审核,2报名审核通过,3报名审核未通过,4完成部分项目考试,5已完成全部考试
     */
    private Integer status;

    /**
     * 审核意见或退回原因
     */
    private String remark;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}