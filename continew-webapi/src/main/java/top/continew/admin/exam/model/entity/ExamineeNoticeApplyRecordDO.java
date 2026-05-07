package top.continew.admin.exam.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 考生报考通知对应项目-计划明细实体
 *
 * @author ilhaha
 * @since 2026/05/07 19:53
 */
@Data
@TableName("ted_examinee_notice_apply_record")
public class ExamineeNoticeApplyRecordDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报名主表ID
     */
    private Long applyId;

    /**
     * 考生ID
     */
    private Long examineeId;

    /**
     * 通知ID
     */
    private Long noticeId;

    /**
     * 报考项目ID
     */
    private Long projectId;

    /**
     * 考试计划ID
     */
    private Long planId;

    /**
     * 考试场次类型：1初试，2补考
     */
    private Integer examAttemptType;

    /**
     * 报考类型位标识:1实操,2拍片,4评片,8开卷,16闭卷
     */
    private Integer practicalType;

    /**
     * 删除标记(0未删,1已删)
     */
    private Boolean isDeleted;
}